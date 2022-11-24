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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Dragon {
    public static HashMap<UUID, Dragon> DRAGONS = new HashMap<>();

    private EnderDragon dragon;
    private Location spawnLocation;
    private Location[] crystalsLocation;
    private ArrayList<EnderCrystal> crystals = new ArrayList<>();
    private HashMap<UUID, Double> damagers = new HashMap<>();

    public Dragon(Location spawnLocation, Location[] crystalsLocation) {
        this.spawnLocation = spawnLocation;
        this.crystalsLocation = crystalsLocation;
    }

    public void spawn() {
        dragon = (EnderDragon) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ENDER_DRAGON);
        dragon.setPodium(spawnLocation);

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
           .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Double getTotalDamages() {
        return damagers.values().stream().reduce((a, b) -> a+b).orElse(0D);
    }

    public Double getDamageDealt(Player player) {
        return damagers.get(player.getUniqueId());
    }

    public void destroy() {
        DRAGONS.remove(dragon.getUniqueId());
    }
}
