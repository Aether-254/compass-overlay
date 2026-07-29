package awa.Aether_254.sleep_vote_skip;

import com.mojang.brigadier.Command;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = SleepVoteSkip.MOD_ID)
public final class SleepVoteManager {
    private static final Map<ResourceKey<Level>, Vote> VOTES = new HashMap<>();

    private SleepVoteManager() {
    }

    @SubscribeEvent
    public static void onSleep(CanPlayerSleepEvent event) {
        if (!SleepVoteConfig.get().enabled || event.getProblem() != null)
            return;
        ServerPlayer player = event.getEntity();
        ServerLevel level = player.serverLevel();
        Vote vote = VOTES.computeIfAbsent(level.dimension(),
            key -> new Vote(level.getGameTime() + SleepVoteConfig.get().voteTimeoutSeconds * 20L));
        if (SleepVoteConfig.get().sleeperVotesYes) {
            vote.no.remove(player.getUUID());
            vote.yes.add(player.getUUID());
        }
        broadcast(level, Component.literal(player.getName().getString()
            + " started a sleep vote. Use /sleep yes or /sleep no.")
            .withStyle(ChatFormatting.GOLD));
        check(level, vote);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("sleep")
            .then(Commands.literal("yes").executes(context -> vote(context.getSource().getPlayerOrException(), true)))
            .then(Commands.literal("no").executes(context -> vote(context.getSource().getPlayerOrException(), false))));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        VOTES.entrySet().removeIf(entry -> {
            ServerLevel level = server.getLevel(entry.getKey());
            if (level == null || level.getGameTime() <= entry.getValue().expiresAt)
                return false;
            broadcast(level, Component.literal("Sleep vote expired.").withStyle(ChatFormatting.RED));
            return true;
        });
    }

    private static int vote(ServerPlayer player, boolean yes) {
        ServerLevel level = player.serverLevel();
        Vote vote = VOTES.get(level.dimension());
        if (vote == null) {
            player.sendSystemMessage(Component.literal("No sleep vote is active.")
                .withStyle(ChatFormatting.GRAY));
            return 0;
        }
        Set<UUID> selected = yes ? vote.yes : vote.no;
        Set<UUID> other = yes ? vote.no : vote.yes;
        other.remove(player.getUUID());
        selected.add(player.getUUID());
        broadcast(level, Component.literal(player.getName().getString() + " voted " + (yes ? "yes" : "no") + ".")
            .withStyle(yes ? ChatFormatting.GREEN : ChatFormatting.RED));
        check(level, vote);
        return Command.SINGLE_SUCCESS;
    }

    private static void check(ServerLevel level, Vote vote) {
        int total = Math.max(1, level.players().size());
        int requiredPercent = SleepVoteConfig.get().yesPercentage;
        broadcast(level, Component.literal("Sleep vote: " + vote.yes.size() + " yes, " + vote.no.size()
            + " no (" + total + " players).").withStyle(ChatFormatting.YELLOW));
        if (vote.yes.size() * 100 <= total * requiredPercent)
            return;
        long nextMorning = (level.getDayTime() / 24000L + 1L) * 24000L;
        level.setDayTime(nextMorning);
        if (SleepVoteConfig.get().clearWeather)
            level.setWeatherParameters(0, 0, false, false);
        for (ServerPlayer player : level.players()) {
            if (player.isSleeping())
                player.stopSleepInBed(false, true);
        }
        broadcast(level, Component.literal("Sleep vote passed. Good morning!")
            .withStyle(ChatFormatting.GREEN));
        VOTES.remove(level.dimension());
    }

    private static void broadcast(ServerLevel level, Component message) {
        level.getServer().getPlayerList().broadcastSystemMessage(message, false);
    }

    private static final class Vote {
        private final long expiresAt;
        private final Set<UUID> yes = new HashSet<>();
        private final Set<UUID> no = new HashSet<>();

        private Vote(long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }
}
