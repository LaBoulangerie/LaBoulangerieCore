package net.laboulangerie.laboulangeriecore.elytra;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class ElytraManager {

    public record RestrictionData(long expiration, RestrictionReason reason) {}

    private final Map<String, RestrictionData> restrictedWorlds = new HashMap<>();
    private final Map<String, BukkitTask> expirationTasks = new HashMap<>();
    private final LaBoulangerieCore plugin;

    public ElytraManager() {
        this.plugin = LaBoulangerieCore.PLUGIN;
        load();
    }

    public void restrictWorld(String worldName, long durationMillis) {
        restrictWorld(worldName, durationMillis, RestrictionReason.COMMAND);
    }

    public void restrictWorld(String worldName, long durationMillis, RestrictionReason reason) {
        long now = System.currentTimeMillis();
        RestrictionData current = restrictedWorlds.get(worldName);
        long currentExp = current != null ? current.expiration() : now;
        long baseTime = Math.max(currentExp, now);
        long newExpiration = baseTime + durationMillis;

        restrictedWorlds.put(worldName, new RestrictionData(newExpiration, reason));
        save();
        scheduleExpiration(worldName, newExpiration - now);
        String message = reason == RestrictionReason.WIND ? getWindMessage() : getCommandMessage();
        forceStopGlidingPlayers(worldName, message);
    }

    public void restrictWorldIndefinitely(String worldName) {
        restrictedWorlds.put(worldName, new RestrictionData(Long.MAX_VALUE, RestrictionReason.COMMAND));
        save();
        cancelExpirationTask(worldName);
        forceStopGlidingPlayers(worldName, getCommandMessage());
    }

    public void unrestrictWorld(String worldName) {
        restrictedWorlds.remove(worldName);
        cancelExpirationTask(worldName);
        save();
    }

    public void restrictWorldByWind(String worldName, long durationMillis) {
        long now = System.currentTimeMillis();
        long newExpiration = now + durationMillis;

        restrictedWorlds.put(worldName, new RestrictionData(newExpiration, RestrictionReason.WIND));
        save();
        scheduleExpiration(worldName, durationMillis);
        forceStopGlidingPlayers(worldName, getWindMessage());
    }

    public void unrestrictWorldByWind(String worldName) {
        RestrictionData data = restrictedWorlds.get(worldName);
        if (data != null && data.reason() == RestrictionReason.WIND) {
            restrictedWorlds.remove(worldName);
            cancelExpirationTask(worldName);
            save();
        }
    }

    public boolean isWindRestricted(String worldName) {
        if (!isWindRestrictionEnabled()) return false;

        RestrictionData data = restrictedWorlds.get(worldName);
        if (data == null) return false;
        if (data.reason() != RestrictionReason.WIND) return false;

        if (data.expiration() != Long.MAX_VALUE && System.currentTimeMillis() >= data.expiration()) {
            unrestrictWorldByWind(worldName);
            return false;
        }
        return true;
    }

    public boolean isRestricted(String worldName) {
        if (!isCommandRestrictionEnabled()) return false;

        RestrictionData data = restrictedWorlds.get(worldName);
        if (data == null) return false;
        if (data.reason() != RestrictionReason.COMMAND) return false;

        if (data.expiration() != Long.MAX_VALUE && System.currentTimeMillis() >= data.expiration()) {
            unrestrictWorld(worldName);
            return false;
        }
        return true;
    }

    public boolean isRestrictedByRain(World world) {
        if (!isRainRestrictionEnabled()) return false;
        return world.hasStorm();
    }

    public RestrictionReason getRestrictionReason(Player player) {
        World world = player.getWorld();
        String worldName = world.getName();

        if (player.hasPermission("laboulangeriecore.elytra.bypass")) {
            return RestrictionReason.NONE;
        }

        if (isWindRestricted(worldName)) {
            return RestrictionReason.WIND;
        }

        if (isRestricted(worldName)) {
            return RestrictionReason.COMMAND;
        }

        if (isRestrictedByRain(world)) {
            return RestrictionReason.RAIN;
        }

        return RestrictionReason.NONE;
    }

    public void forceStopGlidingPlayers(String worldName, String message) {
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return;

        for (Player player : world.getPlayers()) {
            if (player.isGliding() && !player.hasPermission("laboulangeriecore.elytra.bypass")) {
                player.setGliding(false);
                player.sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        }
    }

    public void forceStopGlidingPlayersInWorld(World world, String message) {
        for (Player player : world.getPlayers()) {
            if (player.isGliding() && !player.hasPermission("laboulangeriecore.elytra.bypass")) {
                player.setGliding(false);
                player.sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        }
    }

    public long getRemainingTime(String worldName) {
        RestrictionData data = restrictedWorlds.get(worldName);
        if (data == null) return 0;
        if (data.expiration() == Long.MAX_VALUE) return Long.MAX_VALUE;
        return Math.max(0, data.expiration() - System.currentTimeMillis());
    }

    public Map<String, Long> getRestrictedWorlds() {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, RestrictionData> entry : restrictedWorlds.entrySet()) {
            result.put(entry.getKey(), entry.getValue().expiration());
        }
        return result;
    }

    public RestrictionReason getRestrictionReasonForWorld(String worldName) {
        RestrictionData data = restrictedWorlds.get(worldName);
        if (data == null) return RestrictionReason.NONE;
        return data.reason();
    }

    private void scheduleExpiration(String worldName, long delayMillis) {
        cancelExpirationTask(worldName);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                unrestrictWorld(worldName);
            }
        }.runTaskLater(plugin, delayMillis / 50);

        expirationTasks.put(worldName, task);
    }

    private void cancelExpirationTask(String worldName) {
        BukkitTask task = expirationTasks.remove(worldName);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public void cancelAllTasks() {
        for (BukkitTask task : expirationTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        expirationTasks.clear();
    }

    private void save() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, "elytra-restrictions.yml");
        YamlConfiguration yaml = new YamlConfiguration();

        for (Map.Entry<String, RestrictionData> entry : restrictedWorlds.entrySet()) {
            String worldName = entry.getKey();
            RestrictionData data = entry.getValue();
            yaml.set(worldName + ".expiration", data.expiration());
            yaml.set(worldName + ".reason", data.reason().name());
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save elytra restrictions: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "data/elytra-restrictions.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        long now = System.currentTimeMillis();

        for (String worldName : yaml.getKeys(false)) {
            // Support both old format (direct long) and new format (section with expiration/reason)
            if (yaml.isConfigurationSection(worldName)) {
                long expiration = yaml.getLong(worldName + ".expiration");
                String reasonStr = yaml.getString(worldName + ".reason", "COMMAND");
                RestrictionReason reason;
                try {
                    reason = RestrictionReason.valueOf(reasonStr);
                } catch (IllegalArgumentException e) {
                    reason = RestrictionReason.COMMAND;
                }

                if (expiration == Long.MAX_VALUE) {
                    restrictedWorlds.put(worldName, new RestrictionData(expiration, reason));
                } else if (expiration > now) {
                    restrictedWorlds.put(worldName, new RestrictionData(expiration, reason));
                    scheduleExpiration(worldName, expiration - now);
                }
            } else {
                // Old format: just a long value
                long expiration = yaml.getLong(worldName);
                if (expiration == Long.MAX_VALUE) {
                    restrictedWorlds.put(worldName, new RestrictionData(expiration, RestrictionReason.COMMAND));
                } else if (expiration > now) {
                    restrictedWorlds.put(worldName, new RestrictionData(expiration, RestrictionReason.COMMAND));
                    scheduleExpiration(worldName, expiration - now);
                }
            }
        }
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("elytra-restriction.enabled", true);
    }

    public boolean isRainRestrictionEnabled() {
        return isEnabled() && plugin.getConfig().getBoolean("elytra-restriction.rain-restriction", true);
    }

    public boolean isCommandRestrictionEnabled() {
        return isEnabled() && plugin.getConfig().getBoolean("elytra-restriction.command-restriction", true);
    }

    public String getRainMessage() {
        return plugin.getConfig().getString("elytra-restriction.messages.rain",
                "<red>Impossible de voler pendant la pluie !");
    }

    public String getCommandMessage() {
        return plugin.getConfig().getString("elytra-restriction.messages.command",
                "<red>Les elytras sont temporairement désactivés dans ce monde.");
    }

    public String getRainStartMessage() {
        return plugin.getConfig().getString("elytra-restriction.messages.rain-start",
                "<red>La pluie commence ! Vous ne pouvez plus voler.");
    }

    public boolean isWindRestrictionEnabled() {
        return isEnabled() && plugin.getConfig().getBoolean("elytra-restriction.wind-restriction.enabled", true);
    }

    public String getWindMessage() {
        return plugin.getConfig().getString("elytra-restriction.messages.wind",
                "<red>Les vents sont trop forts pour voler !");
    }

    public enum RestrictionReason {
        NONE,
        RAIN,
        COMMAND,
        WIND
    }
}
