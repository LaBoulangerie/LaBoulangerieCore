package net.laboulangerie.laboulangeriecore.speedpaths;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class SpeedPathFlagRegistry {

    private static final String FLAG_PREFIX = "lbcore-speedpath-";
    private static final Map<String, StateFlag> registeredFlags = new HashMap<>();
    private static boolean worldGuardAvailable = false;

    /**
     * Enregistre les flags WorldGuard pour chaque type de speed-path.
     * Doit être appelé dans onLoad() AVANT que WorldGuard s'active.
     */
    public static void registerFlags(Set<String> pathKeys, Logger logger) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return;
        }

        worldGuardAvailable = true;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        for (String pathKey : pathKeys) {
            String flagName = FLAG_PREFIX + pathKey.toLowerCase();
            try {
                StateFlag flag = new StateFlag(flagName, true); // true = ALLOW par défaut
                registry.register(flag);
                registeredFlags.put(pathKey, flag);
                logger.info("Registered WorldGuard flag: " + flagName);
            } catch (FlagConflictException e) {
                // Le flag existe déjà (probablement d'un reload)
                Flag<?> existing = registry.get(flagName);
                if (existing instanceof StateFlag) {
                    registeredFlags.put(pathKey, (StateFlag) existing);
                    logger.info("Using existing WorldGuard flag: " + flagName);
                } else {
                    logger.warning("Flag " + flagName + " already exists with a different type!");
                }
            }
        }
    }

    /**
     * Vérifie si le speed-path est autorisé pour ce joueur à sa position actuelle.
     * @return true si le speed-path est autorisé (ou si WorldGuard n'est pas installé)
     */
    public static boolean testFlag(Player player, String pathKey) {
        if (!worldGuardAvailable) {
            return true; // WorldGuard non installé, tout est autorisé
        }

        StateFlag flag = registeredFlags.get(pathKey);
        if (flag == null) {
            return true; // Flag non enregistré pour ce chemin, autorisé par défaut
        }

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), flag);
    }

    /**
     * @return true si WorldGuard est disponible
     */
    public static boolean isWorldGuardAvailable() {
        return worldGuardAvailable;
    }

    /**
     * @return les flags enregistrés
     */
    public static Map<String, StateFlag> getRegisteredFlags() {
        return registeredFlags;
    }
}
