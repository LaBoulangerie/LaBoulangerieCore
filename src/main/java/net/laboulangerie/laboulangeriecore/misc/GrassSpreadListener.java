package net.laboulangerie.laboulangeriecore.misc;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class GrassSpreadListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onGrassSpread(BlockSpreadEvent event) {
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();

        if (!config.getBoolean("grass-spread.enabled", true)) {
            return;
        }

        Block source = event.getSource();

        // Seulement la propagation d'herbe (GRASS_BLOCK -> DIRT)
        if (source.getType() != Material.GRASS_BLOCK) {
            return;
        }

        Block block = event.getBlock(); // Le bloc destination (DIRT)

        int minSkylightLevel = config.getInt("grass-spread.min-skylight-level", 12);
        double noSkylightRate = config.getDouble("grass-spread.no-skylight-rate", 0.1);

        if (block.getLightFromSky() < minSkylightLevel) {
            if (ThreadLocalRandom.current().nextDouble() > noSkylightRate) {
                event.setCancelled(true);
            }
        }
    }
}
