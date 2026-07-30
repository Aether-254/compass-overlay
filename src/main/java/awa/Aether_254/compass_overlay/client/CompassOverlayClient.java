package awa.Aether_254.compass_overlay.client;

import awa.Aether_254.compass_overlay.CompassOverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public final class CompassOverlayClient {
    private CompassOverlayClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(CompassOverlayClient::registerLayer);
    }

    private static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("compass_overlay", "overlay"),
            CompassOverlayClient::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        CompassOverlayConfig.Data config = CompassOverlayConfig.get();
        if (!config.enabled || minecraft.player == null || minecraft.options.hideGui)
            return;
        if (config.requireCompass && minecraft.player.getInventory().items.stream().noneMatch(stack -> stack.is(Items.COMPASS)))
            return;

        int degrees = Math.floorMod(Math.round(minecraft.player.getYRot()), 360);
        String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        String cardinal = directions[Math.floorMod((degrees + 22) / 45, 8)];
        String text = cardinal + "  " + minecraft.player.blockPosition().getX() + ", "
            + minecraft.player.blockPosition().getY() + ", " + minecraft.player.blockPosition().getZ();
        int x = (graphics.guiWidth() - minecraft.font.width(text)) / 2 + config.xOffset;
        graphics.drawString(minecraft.font, text, x, config.y, 0xFFFFFFFF, config.shadow);
    }
}
