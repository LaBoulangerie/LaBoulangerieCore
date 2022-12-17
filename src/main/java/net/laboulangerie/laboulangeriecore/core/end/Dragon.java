package net.laboulangerie.laboulangeriecore.core.end;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Dragon {
    public static HashMap<UUID, Dragon> DRAGONS = new HashMap<>();

    private EnderDragon dragon;
    private Location spawnLocation;
    private ArrayList<Location> crystalsLocation;
    private ArrayList<EnderCrystal> crystals = new ArrayList<>();
    private HashMap<UUID, Double> damagers = new HashMap<>();
    private boolean shouldSpawnEgg = false;
    private int health = 0;

    public Dragon(Location spawnLocation, ArrayList<Location> crystalsLocation, int health) {
        this.spawnLocation = spawnLocation;
        this.crystalsLocation = crystalsLocation;
        this.health = health;
    }

    public void spawn() {
        dragon = (EnderDragon) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ENDER_DRAGON);
        dragon.setPodium(spawnLocation);
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        dragon.setHealth(health);

        dragon.setPhase(Phase.CIRCLING);
        BossBar bossBar = BossBar.bossBar(Component.text("Ender Dragon"), 1, Color.PURPLE, Overlay.PROGRESS);

        for (Player player : dragon.getWorld().getPlayers()) player.showBossBar(bossBar);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!dragon.isDead()) {
                    bossBar.progress((float) (dragon.getHealth() / dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                } else {
                    List<Player> players = dragon.getWorld().getPlayers();
                    for (Player player : players) player.hideBossBar(bossBar);
                    cancel();
                }
            }
        }.runTaskTimer(LaBoulangerieCore.PLUGIN, 0, 5);

        DRAGONS.put(dragon.getUniqueId(), this);
    }

    public void spawnCrystals() {
        for (Location location : crystalsLocation) {
            EnderCrystal crystal = (EnderCrystal) spawnLocation.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            crystals.add(crystal);
        }
    }

    public void dealDamage(Player player, double damage) {
        damagers.put(player.getUniqueId(), (damagers.get(player.getUniqueId()) != null ? damagers.get(player.getUniqueId()) : 0) + damage);
    }

    public List<Player> getImplicatedPlayers() {
        return damagers.keySet().stream().map(uuid -> Bukkit.getPlayer(uuid)).collect(Collectors.toList());
    }

    public Map<Player, Double> sortDamagers() {
        return damagers.entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(Bukkit.getPlayer(entry.getKey()), entry.getValue()))
           .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Double getTotalDamages() {
        return damagers.values().stream().reduce((a, b) -> a+b).orElse(0D);
    }

    public Double getDamageDealt(Player player) {
        return damagers.get(player.getUniqueId());
    }

    public void destroy() {
        DRAGONS.remove(dragon.getUniqueId());
        if (!shouldSpawnEgg) return;

        spawnLocation.add(new Vector(0, 1, 0)).getBlock().setType(Material.DRAGON_EGG);
        Location startCorner = spawnLocation.add(-2, -4, -2);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if ((x == 0 && y == 0) || (x == 4 && y == 4) || (x == 0 && y == 4) || (x == 4 && y == 0) || (x == 2 && y == 2)) continue;
                startCorner.clone().add(x, 0, y).getBlock().setType(Material.END_PORTAL);
            }
        }
    }

    public void setShouldSpawnEgg(boolean shouldSpawnEgg) { this.shouldSpawnEgg = shouldSpawnEgg; }
    public boolean shouldSpawnEgg() { return shouldSpawnEgg; }
}
