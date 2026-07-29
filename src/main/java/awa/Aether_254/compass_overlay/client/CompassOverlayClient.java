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
        String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        String cardinal = directions[Math.floorMod((degrees + 22) / 45, 8)];
        String text = cardinal + "  " + minecraft.player.blockPosition().getX() + ", "
            + minecraft.player.blockPosition().getY() + ", " + minecraft.player.blockPosition().getZ();
        int x = (event.getGuiGraphics().guiWidth() - minecraft.font.width(text)) / 2 + config.xOffset;
        event.getGuiGraphics().drawString(minecraft.font, text, x, config.y, 0xFFFFFFFF, config.shadow);
    }
}
