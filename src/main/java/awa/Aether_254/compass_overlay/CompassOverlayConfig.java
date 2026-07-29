package awa.Aether_254.compass_overlay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;

public final class CompassOverlayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("compass_overlay.json");
    private static Data data = new Data();

    private CompassOverlayConfig() {
    }

    public static Data get() {
        return data;
    }

    public static void load() {
        try {
            if (Files.isRegularFile(PATH)) {
                Data loaded = GSON.fromJson(Files.readString(PATH), Data.class);
                data = loaded == null ? new Data() : loaded;
            }
        } catch (IOException | RuntimeException ignored) {
            data = new Data();
        }
        save();
    }

    public static void save() {
        data.xOffset = Math.max(-10000, Math.min(10000, data.xOffset));
        data.y = Math.max(0, Math.min(10000, data.y));
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(data));
        } catch (IOException ignored) {
        }
    }

    public static final class Data {
        public boolean enabled = true;
        public boolean requireCompass = true;
        public int xOffset = 0;
        public int y = 8;
        public boolean shadow = true;
    }
}
