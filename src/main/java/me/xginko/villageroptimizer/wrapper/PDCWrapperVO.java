package me.xginko.villageroptimizer.wrapper;

import me.xginko.villageroptimizer.VillagerOptimizer;
import me.xginko.villageroptimizer.struct.enums.Keyring;
import me.xginko.villageroptimizer.struct.enums.OptimizationType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class PDCWrapperVO extends PDCWrapper {

    PDCWrapperVO(@NotNull Villager villager) {
        super(villager);
    }

    @Override
    public Keyring.Space getSpace() {
        return Keyring.Space.VillagerOptimizer;
    }

    @Override
    public boolean isOptimized() {
        return dataContainer.has(Keyring.VillagerOptimizer.OPTIMIZATION_TYPE.getKey(), PersistentDataType.STRING);
    }

    @Override
    public boolean canOptimize(long cooldown_millis) {
        return System.currentTimeMillis() > getLastOptimize() + cooldown_millis;
    }

    @Override
    public void setOptimizationType(OptimizationType type) {
        VillagerOptimizer.scheduling().entitySpecificScheduler(villager).runAtFixedRate(setOptimization -> {
            // Keep repeating task until villager is no longer trading with a player
            if (villager.isTrading()) return;

            if (type == OptimizationType.NONE) {
                if (isOptimized())
                    dataContainer.remove(Keyring.VillagerOptimizer.OPTIMIZATION_TYPE.getKey());
                villager.setAware(true);
                villager.setAI(true);
            } else {
                dataContainer.set(Keyring.VillagerOptimizer.OPTIMIZATION_TYPE.getKey(), PersistentDataType.STRING, type.name());
                villager.setAware(false);
            }

            // End repeating task once logic is finished
            setOptimization.cancel();
        }, null, 1L, 20L);
    }

    @Override
    public @NotNull OptimizationType getOptimizationType() {
        if (isOptimized()) {
            return OptimizationType.valueOf(dataContainer.get(Keyring.VillagerOptimizer.OPTIMIZATION_TYPE.getKey(), PersistentDataType.STRING));
        } else {
            return OptimizationType.NONE;
        }
    }

    @Override
    public void saveOptimizeTime() {
        dataContainer.set(Keyring.VillagerOptimizer.LAST_OPTIMIZE_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG, System.currentTimeMillis());
    }

    /**
     * @return The system time in millis when the villager was last optimized, 0L if the villager was never optimized.
     */
    private long getLastOptimize() {
        if (dataContainer.has(Keyring.VillagerOptimizer.LAST_OPTIMIZE_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG)) {
            return dataContainer.get(Keyring.VillagerOptimizer.LAST_OPTIMIZE_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG);
        }
        return 0L;
    }

    @Override
    public long getOptimizeCooldownMillis(long cooldown_millis) {
        if (getLastOptimize() > 0L) {
            return cooldown_millis - (System.currentTimeMillis() - getLastOptimize());
        }
        return cooldown_millis;
    }

    @Override
    public long getLastRestockFullTime() {
        if (dataContainer.has(Keyring.VillagerOptimizer.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG)) {
            return dataContainer.get(Keyring.VillagerOptimizer.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG);
        }
        return 0L;
    }

    @Override
    public void saveRestockTime() {
        dataContainer.set(Keyring.VillagerOptimizer.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG, villager.getWorld().getFullTime());
    }

    @Override
    public boolean canLevelUp(long cooldown_millis) {
        return System.currentTimeMillis() >= getLastLevelUpTime() + cooldown_millis;
    }

    @Override
    public void saveLastLevelUp() {
        dataContainer.set(Keyring.VillagerOptimizer.LAST_LEVELUP_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG, System.currentTimeMillis());
    }

    /**
     * @return The systime in millis when the entity was last leveled up.
     */
    private long getLastLevelUpTime() {
        if (dataContainer.has(Keyring.VillagerOptimizer.LAST_LEVELUP_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG))
            return dataContainer.get(Keyring.VillagerOptimizer.LAST_LEVELUP_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG);
        return 0L;
    }

    @Override
    public long getLevelCooldownMillis(long cooldown_millis) {
        if (dataContainer.has(Keyring.VillagerOptimizer.LAST_LEVELUP_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG))
            return System.currentTimeMillis() - (dataContainer.get(Keyring.VillagerOptimizer.LAST_LEVELUP_SYSTIME_MILLIS.getKey(), PersistentDataType.LONG) + cooldown_millis);
        return cooldown_millis;
    }
}