package awa.Aether_254.compass_overlay;

import awa.Aether_254.compass_overlay.client.CompassOverlayClient;
import awa.Aether_254.compass_overlay.client.CompassOverlayConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;

@Mod(CompassOverlay.MOD_ID)
public final class CompassOverlay {
    public static final String MOD_ID = "compass_overlay";

    public CompassOverlay(IEventBus modBus, ModContainer container) {
        CompassOverlayConfig.load();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CompassOverlayConfigScreen.register(container);
            CompassOverlayClient.register(modBus);
        }
    }
}
