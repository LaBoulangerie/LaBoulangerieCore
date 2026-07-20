package net.laboulangerie.laboulangeriecore.misc;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class CropGrowthListener implements Listener {

    private static final Set<Material> CROPS = Set.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM,
            Material.MELON,
            Material.PUMPKIN,
            Material.NETHER_WART,
            Material.SWEET_BERRY_BUSH,
            Material.TORCHFLOWER_CROP,
            Material.PITCHER_CROP,
            Material.SUGAR_CANE);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onCropGrow(BlockGrowEvent event) {
        FileConfiguration config = LaBoulangerieCore.PLUGIN.getConfig();

        if (!config.getBoolean("crop-growth.enabled", true)) {
            return;
        }

        Block block = event.getBlock();

        if (!CROPS.contains(block.getType())) {
            return;
        }

        int minSkylightLevel = config.getInt("crop-growth.min-skylight-level", 12);
        double noSkylightRate = config.getDouble("crop-growth.no-skylight-rate", 0.1);

        if (block.getLightFromSky() < minSkylightLevel) {
            if (ThreadLocalRandom.current().nextDouble() > noSkylightRate) {
                event.setCancelled(true);
            }
        }
    }
}
