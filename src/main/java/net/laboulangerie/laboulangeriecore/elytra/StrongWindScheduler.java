package net.laboulangerie.laboulangeriecore.elytra;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class StrongWindScheduler {

    private final Map<String, BukkitTask> worldCheckTasks = new HashMap<>();
    private final LaBoulangerieCore plugin;
    private final ElytraManager elytraManager;
    private final Random random = new Random();

    public StrongWindScheduler(LaBoulangerieCore plugin, ElytraManager elytraManager) {
        this.plugin = plugin;
        this.elytraManager = elytraManager;
    }

    public void start() {
        if (!elytraManager.isWindRestrictionEnabled()) {
            plugin.getLogger().info("Wind restriction is disabled, skipping scheduler start.");
            return;
        }

        ConfigurationSection worldsSection = plugin.getConfig()
                .getConfigurationSection("elytra-restriction.wind-restriction.worlds");

        if (worldsSection == null) {
            plugin.getLogger().info("No worlds configured for wind restriction.");
            return;
        }

        Set<String> worlds = worldsSection.getKeys(false);
        for (String worldName : worlds) {
            startWorldScheduler(worldName);
        }

        plugin.getLogger().info("Strong wind scheduler started for " + worlds.size() + " world(s).");
    }

    public void stop() {
        for (BukkitTask task : worldCheckTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        worldCheckTasks.clear();
        plugin.getLogger().info("Strong wind scheduler stopped.");
    }

    private void startWorldScheduler(String worldName) {
        int checkInterval = getWorldSetting(worldName, "check-interval", 300);
        long intervalTicks = checkInterval * 20L;

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                checkAndTriggerWind(worldName);
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);

        worldCheckTasks.put(worldName, task);
    }

    private void checkAndTriggerWind(String worldName) {
        if (!elytraManager.isWindRestrictionEnabled()) return;
        if (elytraManager.isWindRestricted(worldName)) return;

        double probability = getWorldSetting(worldName, "trigger-probability", 0.15);

        if (random.nextDouble() < probability) {
            int durationMin = getWorldSetting(worldName, "duration-min", 300);
            int durationMax = getWorldSetting(worldName, "duration-max", 7200);

            int durationSeconds = durationMin + random.nextInt(Math.max(1, durationMax - durationMin + 1));
            long durationMillis = durationSeconds * 1000L;

            triggerWind(worldName, durationMillis);

            plugin.getLogger().info("Strong wind triggered in " + worldName + " for " + durationSeconds + " seconds.");
        }
    }

    public void triggerWind(String worldName, long durationMillis) {
        elytraManager.restrictWorldByWind(worldName, durationMillis);
    }

    public void stopWind(String worldName) {
        elytraManager.unrestrictWorldByWind(worldName);
    }

    public boolean isWindActive(String worldName) {
        return elytraManager.isWindRestricted(worldName);
    }

    @SuppressWarnings("unchecked")
    private <T> T getWorldSetting(String worldName, String key, T defaultValue) {
        ConfigurationSection defaultSettings = plugin.getConfig()
                .getConfigurationSection("elytra-restriction.wind-restriction.default-settings");

        ConfigurationSection worldSettings = plugin.getConfig()
                .getConfigurationSection("elytra-restriction.wind-restriction.worlds." + worldName);

        // First check world-specific setting
        if (worldSettings != null && worldSettings.contains(key)) {
            Object value = worldSettings.get(key);
            if (value != null) {
                return (T) value;
            }
        }

        // Fall back to default settings
        if (defaultSettings != null && defaultSettings.contains(key)) {
            Object value = defaultSettings.get(key);
            if (value != null) {
                return (T) value;
            }
        }

        return defaultValue;
    }
}
