package me.xginko.villageroptimizer.wrapper;

import me.xginko.villageroptimizer.VillagerOptimizer;
import me.xginko.villageroptimizer.struct.enums.Keyring;
import me.xginko.villageroptimizer.struct.enums.OptimizationType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class PDCWrapperAVL extends PDCWrapper {

    PDCWrapperAVL(@NotNull Villager villager) {
        super(villager);
    }

    @Override
    public Keyring.Space getSpace() {
        return Keyring.Space.AntiVillagerLag;
    }

    @Override
    public boolean isOptimized() {
        return dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_ANY.getKey(), PersistentDataType.STRING)
                || dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey(), PersistentDataType.STRING)
                || dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey(), PersistentDataType.STRING);
    }

    @Override
    public boolean canOptimize(long cooldown_millis) {
        return !dataContainer.has(Keyring.AntiVillagerLag.NEXT_OPTIMIZATION_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG)
                || System.currentTimeMillis() > TimeUnit.SECONDS.toMillis(dataContainer.get(Keyring.AntiVillagerLag.NEXT_OPTIMIZATION_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG));
    }

    @Override
    public void setOptimizationType(OptimizationType type) {
        VillagerOptimizer.scheduling().entitySpecificScheduler(villager).runAtFixedRate(setOptimization -> {
            // Keep repeating task until villager is no longer trading with a player
            if (villager.isTrading()) return;

            if (type == OptimizationType.NONE) {
                if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_ANY.getKey(), PersistentDataType.STRING))
                    dataContainer.remove(Keyring.AntiVillagerLag.OPTIMIZED_ANY.getKey());
                if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey(), PersistentDataType.STRING))
                    dataContainer.remove(Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey());
                if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey(), PersistentDataType.STRING))
                    dataContainer.remove(Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey());

                villager.setAware(true);
                villager.setAI(true);
            } else {
                switch (type) {
                    case BLOCK:
                        dataContainer.set(Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey(), PersistentDataType.STRING, Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey().toString());
                        break;
                    case WORKSTATION:
                        dataContainer.set(Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey(), PersistentDataType.STRING, Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey().toString());
                        break;
                    case COMMAND:
                    case NAMETAG:
                        dataContainer.set(Keyring.AntiVillagerLag.OPTIMIZED_ANY.getKey(), PersistentDataType.STRING, "AVL");
                        break;
                }

                villager.setAware(false);
            }

            // End repeating task once logic is finished
            setOptimization.cancel();
        }, null, 1L, 20L);
    }

    @Override
    public @NotNull OptimizationType getOptimizationType() {
        if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_BLOCK.getKey(), PersistentDataType.STRING)) {
            return OptimizationType.BLOCK;
        }
        if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_WORKSTATION.getKey(), PersistentDataType.STRING)) {
            return OptimizationType.WORKSTATION;
        }
        if (dataContainer.has(Keyring.AntiVillagerLag.OPTIMIZED_ANY.getKey(), PersistentDataType.STRING)) {
            return OptimizationType.COMMAND; // Best we can do
        }
        return OptimizationType.NONE;
    }

    @Override
    public void saveOptimizeTime() {
        // We do nothing here to not break stuff
    }

    @Override
    public long getOptimizeCooldownMillis(long cooldown_millis) {
        if (dataContainer.has(Keyring.AntiVillagerLag.NEXT_OPTIMIZATION_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG)) {
            return TimeUnit.SECONDS.toMillis(dataContainer.get(Keyring.AntiVillagerLag.NEXT_OPTIMIZATION_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG) - System.currentTimeMillis());
        }
        return cooldown_millis;
    }

    @Override
    public long getLastRestockFullTime() {
        if (dataContainer.has(Keyring.AntiVillagerLag.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG)) {
            return dataContainer.get(Keyring.AntiVillagerLag.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG);
        }
        return 0L;
    }

    @Override
    public void saveRestockTime() {
        dataContainer.set(Keyring.AntiVillagerLag.LAST_RESTOCK_WORLD_FULLTIME.getKey(), PersistentDataType.LONG, villager.getWorld().getFullTime());
    }

    @Override
    public boolean canLevelUp(long cooldown_millis) {
        return !dataContainer.has(Keyring.AntiVillagerLag.NEXT_LEVELUP_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG)
                || System.currentTimeMillis() > TimeUnit.SECONDS.toMillis(dataContainer.get(Keyring.AntiVillagerLag.NEXT_LEVELUP_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG));
    }

    @Override
    public void saveLastLevelUp() {
        // We do nothing here to not break stuff
    }

    @Override
    public long getLevelCooldownMillis(long cooldown_millis) {
        if (dataContainer.has(Keyring.AntiVillagerLag.NEXT_LEVELUP_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG))
            return System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(dataContainer.get(Keyring.AntiVillagerLag.NEXT_LEVELUP_SYSTIME_SECONDS.getKey(), PersistentDataType.LONG));
        return cooldown_millis;
    }
}