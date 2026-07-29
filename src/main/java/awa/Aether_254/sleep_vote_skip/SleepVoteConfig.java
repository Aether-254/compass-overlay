package awa.Aether_254.sleep_vote_skip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;

public final class SleepVoteConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("sleep_vote_skip.json");
    private static Data data = new Data();

    private SleepVoteConfig() {
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
        data.yesPercentage = Math.max(1, Math.min(100, data.yesPercentage));
        data.voteTimeoutSeconds = Math.max(10, Math.min(600, data.voteTimeoutSeconds));
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(data));
        } catch (IOException ignored) {
        }
    }

    public static final class Data {
        public boolean enabled = true;
        public int yesPercentage = 50;
        public int voteTimeoutSeconds = 60;
        public boolean sleeperVotesYes = true;
        public boolean clearWeather = true;
    }
}
