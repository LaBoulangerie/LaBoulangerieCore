package net.laboulangerie.laboulangeriecore.betonquest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.QuestException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.boss.DragonBattle.RespawnPhase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderCrystal;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class SummonEndCrystalEvent implements PlayerAction {

    private static final Logger LOGGER = Logger.getLogger("LaBoulangerieCore");

    private final String worldName;
    private final String direction;

    public SummonEndCrystalEvent(String worldName, String direction) {
        this.worldName = worldName;
        this.direction = direction;
    }

    private int getPortalY() {
        return LaBoulangerieCore.PLUGIN.getConfig().getInt("betonquest.end-crystal.portal-y", 58);
    }

    private double getSearchRadius() {
        return LaBoulangerieCore.PLUGIN.getConfig().getDouble("betonquest.end-crystal.search-radius", 1.0);
    }

    private int[] getDirectionPosition(String direction) {
        ConfigurationSection section = LaBoulangerieCore.PLUGIN.getConfig()
            .getConfigurationSection("betonquest.end-crystal.positions." + direction);
        if (section == null) {
            return switch (direction) {
                case "east" -> new int[]{3, 0};
                case "west" -> new int[]{-3, 0};
                case "north" -> new int[]{0, -3};
                case "south" -> new int[]{0, 3};
                default -> null;
            };
        }
        return new int[]{section.getInt("x"), section.getInt("z")};
    }

    private int[][] getCrystalPositions() {
        ConfigurationSection positions = LaBoulangerieCore.PLUGIN.getConfig()
            .getConfigurationSection("betonquest.end-crystal.positions");
        if (positions == null) {
            return new int[][]{{0, 3}, {0, -3}, {3, 0}, {-3, 0}};
        }
        return positions.getKeys(false).stream()
            .map(key -> getDirectionPosition(key))
            .filter(pos -> pos != null)
            .toArray(int[][]::new);
    }

    @Override
    public void execute(Profile profile) throws QuestException {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new QuestException("World not found: " + worldName);
        }
        if (world.getEnvironment() != World.Environment.THE_END) {
            throw new QuestException("World must be THE_END: " + worldName);
        }

        int[] pos = getDirectionPosition(direction.toLowerCase());
        if (pos == null) {
            throw new QuestException("Invalid direction: " + direction + ". Valid: east, west, north, south");
        }

        Location loc = new Location(world, pos[0] + 0.5, getPortalY(), pos[1] + 0.5);
        LOGGER.info("[SummonEndCrystal] Spawning crystal at: " + loc + " (direction: " + direction + ")");

        world.spawn(loc, EnderCrystal.class, crystal -> {
            crystal.setShowingBottom(false);
        });

        checkAndInitiateRespawn(world);
    }

    private void checkAndInitiateRespawn(World world) {
        DragonBattle battle = world.getEnderDragonBattle();
        if (battle == null || battle.getRespawnPhase() != RespawnPhase.NONE) {
            return;
        }

        Set<EnderCrystal> portalCrystals = findPortalCrystals(world);
        LOGGER.info("[SummonEndCrystal] Found " + portalCrystals.size() + " unique crystals around portal");
        for (EnderCrystal c : portalCrystals) {
            LOGGER.info("[SummonEndCrystal] Crystal at: " + c.getLocation());
        }

        if (portalCrystals.size() == 4) {
            LOGGER.info("[SummonEndCrystal] Initiating dragon respawn!");
            battle.initiateRespawn(new ArrayList<>(portalCrystals));
        }
    }

    private Set<EnderCrystal> findPortalCrystals(World world) {
        Set<EnderCrystal> crystals = new HashSet<>();
        for (int[] pos : getCrystalPositions()) {
            Location checkLoc = new Location(world, pos[0], getPortalY(), pos[1]);
            Collection<EnderCrystal> found = world.getNearbyEntitiesByType(
                EnderCrystal.class, checkLoc, getSearchRadius());
            LOGGER.info("[SummonEndCrystal] Searching at: " + checkLoc + ", found: " + found.size());
            crystals.addAll(found);
        }
        return crystals;
    }
}
