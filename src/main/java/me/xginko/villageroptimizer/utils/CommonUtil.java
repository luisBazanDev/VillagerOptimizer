package me.xginko.villageroptimizer.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class CommonUtil {
    public static @NotNull String formatDuration(Duration duration) {
        final int seconds = duration.toSecondsPart();
        final int minutes = duration.toMinutesPart();
        final int hours = duration.toHoursPart();

        if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02dm %02ds", minutes, seconds);
        } else {
            return String.format("%02ds", seconds);
        }
    }

    public static String formatLocation(@NotNull Location location) {
        return "[" + location.getWorld().getName() + "] x=" + location.getBlockX() + ", y=" + location.getBlockY() + ", z=" + location.getBlockZ();
    }

    private static boolean specificChunkLoadedMethodAvailable = true;
    public static boolean isEntitiesLoaded(@NotNull Chunk chunk) {
        if (!specificChunkLoadedMethodAvailable) {
            return chunk.isLoaded();
        }
        try {
            return chunk.isEntitiesLoaded();
        } catch (NoSuchMethodError e) {
            specificChunkLoadedMethodAvailable = false;
            return chunk.isLoaded();
        }
    }

    public static void shakeHead(@NotNull Villager villager) {
        try {
            villager.shakeHead();
        } catch (NoSuchMethodError ignored) {}
    }

    public static Villager.Profession getWorkstationProfession(@NotNull Material workstation) {
        return switch (workstation) {
            case BARREL -> Villager.Profession.FISHERMAN;
            case CARTOGRAPHY_TABLE -> Villager.Profession.CARTOGRAPHER;
            case SMOKER -> Villager.Profession.BUTCHER;
            case SMITHING_TABLE -> Villager.Profession.TOOLSMITH;
            case GRINDSTONE -> Villager.Profession.WEAPONSMITH;
            case BLAST_FURNACE -> Villager.Profession.ARMORER;
            case CAULDRON -> Villager.Profession.LEATHERWORKER;
            case BREWING_STAND -> Villager.Profession.CLERIC;
            case COMPOSTER -> Villager.Profession.FARMER;
            case FLETCHING_TABLE -> Villager.Profession.FLETCHER;
            case LOOM -> Villager.Profession.SHEPHERD;
            case LECTERN -> Villager.Profession.LIBRARIAN;
            case STONECUTTER -> Villager.Profession.MASON;
            default -> Villager.Profession.NONE;
        };
    }
}
