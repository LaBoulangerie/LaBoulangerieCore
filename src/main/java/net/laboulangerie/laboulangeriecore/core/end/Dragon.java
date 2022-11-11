package net.laboulangerie.laboulangeriecore.core.end;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

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

        DragonBattle battle = spawnLocation.getWorld().getEnderDragonBattle();
    }

    public void spawnCrystals() {
        for (Location location : crystalsLocation) {
            EnderCrystal crystal = (EnderCrystal) spawnLocation.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            crystals.add(crystal);
        }
    }
}
