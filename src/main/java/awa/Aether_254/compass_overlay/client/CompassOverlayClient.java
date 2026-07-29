package awa.Aether_254.compass_overlay.client;

import awa.Aether_254.compass_overlay.CompassOverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class CompassOverlayClient {
    private CompassOverlayClient() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(CompassOverlayClient::render);
    }

    private static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        CompassOverlayConfig.Data config = CompassOverlayConfig.get();
        if (!config.enabled || minecraft.player == null || minecraft.options.hideGui)
            return;
        if (config.requireCompass && !minecraft.player.getInventory().contains(Items.COMPASS.getDefaultInstance()))
            return;

        int degrees = Math.floorMod(Math.round(minecraft.player.getYRot()), 360);
        String cardinal = degrees < 45 || degrees >= 315 ? "S"
            : degrees < 135 ? "W" : degrees < 225 ? "N" : "E";
        event.getGuiGraphics().drawString(minecraft.font, cardinal + "  " + degrees + "\u00b0",
            config.x, config.y, 0xFFFFFFFF, config.shadow);
    }
}
