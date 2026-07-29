package awa.Aether_254.sleep_vote_skip.client;

import awa.Aether_254.sleep_vote_skip.SleepVoteConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class SleepVoteConfigScreen {
    private SleepVoteConfigScreen() {
    }

    public static void register(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> create(parent));
    }

    private static Screen create(Screen parent) {
        SleepVoteConfig.Data config = SleepVoteConfig.get();
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
            .setTitle(Component.literal("Sleep Vote Skip"));
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Voting"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        category.addEntry(entries.startBooleanToggle(Component.literal("Enable sleep votes"), config.enabled)
            .setDefaultValue(true).setSaveConsumer(value -> config.enabled = value).build());
        category.addEntry(entries.startIntField(Component.literal("Required yes percentage"), config.yesPercentage)
            .setDefaultValue(50).setMin(1).setMax(100)
            .setTooltip(Component.literal("The vote passes only when yes votes are greater than this percentage."))
            .setSaveConsumer(value -> config.yesPercentage = value).build());
        category.addEntry(entries.startIntField(Component.literal("Vote timeout seconds"), config.voteTimeoutSeconds)
            .setDefaultValue(60).setMin(10).setMax(600)
            .setSaveConsumer(value -> config.voteTimeoutSeconds = value).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Sleeper automatically votes yes"),
                config.sleeperVotesYes)
            .setDefaultValue(true).setSaveConsumer(value -> config.sleeperVotesYes = value).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Clear weather on pass"), config.clearWeather)
            .setDefaultValue(true).setSaveConsumer(value -> config.clearWeather = value).build());
        builder.setSavingRunnable(SleepVoteConfig::save);
        return builder.build();
    }
}
