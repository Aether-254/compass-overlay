package awa.Aether_254.compass_overlay.client;

import awa.Aether_254.compass_overlay.CompassOverlayConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class CompassOverlayConfigScreen {
    private CompassOverlayConfigScreen() {
    }

    public static void register(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> create(parent));
    }

    private static Screen create(Screen parent) {
        CompassOverlayConfig.Data config = CompassOverlayConfig.get();
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
            .setTitle(Component.literal("Compass Overlay"));
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Overlay"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        category.addEntry(entries.startBooleanToggle(Component.literal("Enabled"), config.enabled)
            .setDefaultValue(true).setSaveConsumer(value -> config.enabled = value).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Require compass in inventory"),
                config.requireCompass).setDefaultValue(true)
            .setSaveConsumer(value -> config.requireCompass = value).build());
        category.addEntry(entries.startIntField(Component.literal("Horizontal offset"), config.xOffset)
            .setDefaultValue(0).setMin(-10000).setMax(10000)
            .setSaveConsumer(value -> config.xOffset = value).build());
        category.addEntry(entries.startIntField(Component.literal("Y"), config.y).setDefaultValue(8)
            .setMin(0).setMax(10000).setSaveConsumer(value -> config.y = value).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Text shadow"), config.shadow)
            .setDefaultValue(true).setSaveConsumer(value -> config.shadow = value).build());
        builder.setSavingRunnable(CompassOverlayConfig::save);
        return builder.build();
    }
}
