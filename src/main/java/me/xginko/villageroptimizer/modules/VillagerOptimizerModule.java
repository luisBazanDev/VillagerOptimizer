package me.xginko.villageroptimizer.modules;

import com.github.benmanes.caffeine.cache.Cache;
import me.xginko.villageroptimizer.VillagerOptimizer;
import me.xginko.villageroptimizer.config.Config;
import me.xginko.villageroptimizer.struct.Disableable;
import me.xginko.villageroptimizer.struct.Enableable;
import me.xginko.villageroptimizer.wrapper.WrappedVillager;
import org.bukkit.entity.Villager;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public abstract class VillagerOptimizerModule implements Enableable, Disableable {

    private static final Reflections MODULES_PACKAGE = new Reflections(VillagerOptimizerModule.class.getPackage().getName());
    public static final Set<VillagerOptimizerModule> ENABLED_MODULES = new HashSet<>();

    public abstract boolean shouldEnable();

    protected final VillagerOptimizer plugin;
    protected final Config config;
    protected final Cache<Villager, WrappedVillager> wrapperCache;
    protected final GracefulScheduling scheduling;
    public final String configPath;
    private final String logFormat;

    public VillagerOptimizerModule(String configPath) {
        this.plugin = VillagerOptimizer.getInstance();
        this.config = VillagerOptimizer.config();
        this.wrapperCache = VillagerOptimizer.wrappers();
        this.scheduling = VillagerOptimizer.scheduling();
        this.configPath = configPath;
        shouldEnable(); // Ensure enable option is always first
        String[] paths = configPath.split("\\.");
        if (paths.length <= 2) {
            this.logFormat = "<" + configPath + "> {}";
        } else {
            this.logFormat = "<" + paths[paths.length - 2] + "." + paths[paths.length - 1] + "> {}";
        }
    }

    public static void reloadModules() {
        ENABLED_MODULES.forEach(VillagerOptimizerModule::disable);
        ENABLED_MODULES.clear();

        for (Class<?> clazz : MODULES_PACKAGE.get(Scanners.SubTypes.of(VillagerOptimizerModule.class).asClass())) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

            try {
                VillagerOptimizerModule module = (VillagerOptimizerModule) clazz.getDeclaredConstructor().newInstance();
                if (module.shouldEnable()) {
                    ENABLED_MODULES.add(module);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                VillagerOptimizer.logger().error("Failed initialising module class '{}'.", clazz.getSimpleName(), e);
            }
        }

        ENABLED_MODULES.forEach(VillagerOptimizerModule::enable);
    }

    protected void error(String message, Throwable throwable) {
        VillagerOptimizer.logger().error(logFormat, message, throwable);
    }

    protected void error(String message) {
        VillagerOptimizer.logger().error(logFormat, message);
    }

    protected void warn(String message) {
        VillagerOptimizer.logger().warn(logFormat, message);
    }

    protected void info(String message) {
        VillagerOptimizer.logger().info(logFormat, message);
    }

    protected void notRecognized(Class<?> clazz, String unrecognized) {
        warn("Unable to parse " + clazz.getSimpleName() + " at '" + unrecognized + "'. Please check your configuration.");
    }
}
