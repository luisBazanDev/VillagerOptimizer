package me.xginko.villageroptimizer.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.villageroptimizer.VillagerOptimizer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

public class Config {

    private final @NotNull ConfigFile config;
    public final @NotNull Locale default_lang;
    public final @NotNull Duration cache_keep_time;
    public final boolean auto_lang, support_other_plugins;

    public Config() throws Exception {
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(VillagerOptimizer.getInstance().getDataFolder(), "config.yml"));

        structureConfig();

        this.default_lang = Locale.forLanguageTag(
                getString("general.default-language", "en_us",
                        "The default language that will be used if auto-language is false\n" +
                                "or no matching language file was found.")
                        .replace("_", "-"));
        this.auto_lang = getBoolean("general.auto-language", true,
                "If set to true, will display messages based on client language");
        this.cache_keep_time = Duration.ofSeconds(Math.max(1, getInt("general.cache-keep-time-seconds", 30,
                "The amount of time in seconds a villager will be kept in the plugin's cache.")));
        this.support_other_plugins = getBoolean("general.support-avl-villagers", false,
                "Enable if you have previously used AntiVillagerLag\n" +
                        "(https://www.spigotmc.org/resources/antivillagerlag.102949/).\n" +
                        "Tries to read pre-existing info like optimization state so players\n" +
                        "don't need to reoptimize their villagers.");
    }

    public void saveConfig() {
        try {
            this.config.save();
        } catch (Throwable throwable) {
            VillagerOptimizer.logger().error("Failed to save config file!", throwable);
        }
    }

    private void structureConfig() {
        this.config.addDefault("config-version", 1.00);
        this.createTitledSection("General", "general");
        this.createTitledSection("Optimization", "optimization-methods");
        this.config.addDefault("optimization-methods.commands.unoptimizevillagers", null);
        this.config.addComment("optimization-methods.commands",
                "If you want to disable commands, negate the following permissions:\n" +
                "villageroptimizer.cmd.optimize\n" +
                "villageroptimizer.cmd.unoptimize");
        this.config.addDefault("optimization-methods.nametag-optimization.enable", true);
        this.createTitledSection("Villager Chunk Limit", "villager-chunk-limit");
        this.createTitledSection("Gameplay", "gameplay");
        this.config.addDefault("gameplay.prevent-trading-with-unoptimized.enable", false);
        this.config.addDefault("gameplay.restock-optimized-trades", null);
        this.config.addDefault("gameplay.level-optimized-profession", null);
        this.config.addDefault("gameplay.unoptimize-on-job-loose.enable", true);
        this.config.addDefault("gameplay.villagers-can-be-leashed.enable", true);
        this.config.addDefault("gameplay.villagers-spawn-as-adults.enable", false);
        this.config.addDefault("gameplay.rename-optimized-villagers.enable", false);
        this.config.addDefault("gameplay.prevent-entities-from-targeting-optimized.enable", true);
        this.config.addDefault("gameplay.prevent-damage-to-optimized.enable", true);
    }

    public void createTitledSection(@NotNull String title, @NotNull String path) {
        this.config.addSection(title);
        this.config.addDefault(path, null);
    }

    public @NotNull ConfigFile master() {
        return this.config;
    }

    public boolean getBoolean(@NotNull String path, boolean def, @NotNull String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getBoolean(path, def);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def, @NotNull String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getString(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, def);
    }

    public double getDouble(@NotNull String path, @NotNull Double def, @NotNull String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getDouble(path, def);
    }

    public double getDouble(@NotNull String path, @NotNull Double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, def);
    }

    public int getInt(@NotNull String path, int def, @NotNull String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getInteger(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        this.config.addDefault(path, def);
        return this.config.getInteger(path, def);
    }

    public @NotNull <T> List<T> getList(@NotNull String path, @NotNull List<T> def, @NotNull String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getList(path);
    }

    public @NotNull <T> List<T> getList(@NotNull String path, @NotNull List<T> def) {
        this.config.addDefault(path, def);
        return this.config.getList(path);
    }
}
