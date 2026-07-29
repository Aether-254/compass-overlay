package awa.Aether_254.sleep_vote_skip;

import awa.Aether_254.sleep_vote_skip.client.SleepVoteConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(SleepVoteSkip.MOD_ID)
public final class SleepVoteSkip {
    public static final String MOD_ID = "sleep_vote_skip";

    public SleepVoteSkip(ModContainer container) {
        SleepVoteConfig.load();
        if (FMLEnvironment.dist == Dist.CLIENT)
            SleepVoteConfigScreen.register(container);
    }
}
