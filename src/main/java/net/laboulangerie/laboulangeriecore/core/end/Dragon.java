package net.laboulangerie.laboulangeriecore.core.end;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
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
    EnderDragon dragon;
    Location spawnLocation;
    Location[] crystalsLocation;
    ArrayList<EnderCrystal> crystals = new ArrayList<>();

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
                    bossBar.progress((float) (dragon.getHealth() / dragon.getMaxHealth()));
                } else {
                    List<Player> players = dragon.getWorld().getPlayers();
                    for (Player player : players) player.hideBossBar(bossBar);
                    cancel();
                }
            }
        }.runTaskTimer(LaBoulangerieCore.PLUGIN, 0, 5); 
    }

    public void spawnCrystals() {
        for (Location location : crystalsLocation) {
            EnderCrystal crystal = (EnderCrystal) spawnLocation.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            crystals.add(crystal);
        }
    }
}
