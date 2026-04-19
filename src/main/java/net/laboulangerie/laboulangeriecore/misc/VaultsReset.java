package net.laboulangerie.laboulangeriecore.misc;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;

public class VaultsReset {

    public static void reset() {
        List<String> regionWorldPairs = LaBoulangerieCore.PLUGIN.getConfig().getStringList("vault-reset-regions");

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        for (String regionWorldPair : regionWorldPairs) {
            String[] splitted = regionWorldPair.split(":");
            String worldName = splitted[0];
            String regionName = splitted[1];

            World world = LaBoulangerieCore.PLUGIN.getServer().getWorld(worldName);
            if (world == null) {
                LaBoulangerieCore.PLUGIN.getLogger().warning("World " + worldName + " not found, skipping vault reset region...");
                continue;
            }

            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if (regions == null) {
                LaBoulangerieCore.PLUGIN.getLogger().warning("No regions found in world " + worldName + ", skipping...");
                continue;
            }

            ProtectedRegion region = regions.getRegion(regionName);
            if (region == null) {
                LaBoulangerieCore.PLUGIN.getLogger().warning("Region " + regionName + " not found in world " + worldName + ", skipping...");
                continue;
            }
            // check if region is a cuboid
            if (region instanceof ProtectedCuboidRegion) {
                resetVault(region, world);
            } else {
                LaBoulangerieCore.PLUGIN.getLogger().warning("Region " + regionName + " in world " + worldName
                        + " is not a cuboid region, skipping...");
            }
        }
    }

    private static void resetVault(ProtectedRegion region, World world) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        CraftWorld craftWorld = (CraftWorld) world;
        for (int x = min.x(); x <= max.x(); x++) {
            for (int y = min.y(); y <= max.y(); y++) {
                for (int z = min.z(); z <= max.z(); z++) {
                    if (world.getBlockAt(x, y, z).getType() == Material.VAULT) {
                        LaBoulangerieCore.PLUGIN.getLogger()
                                .info("Resetting rewarded players of vault at " + x + " " + y + " " + z + " in world "
                                        + world.getName());
                        BlockPos blockPos = new BlockPos(x, y, z);
                        BlockEntity blockEntity = craftWorld.getHandle().getBlockEntity(blockPos);

                        if (blockEntity instanceof VaultBlockEntity vault) {
                            vault.getServerData().getRewardedPlayers().clear();
                            vault.setChanged();
                        }
                    }
                }
            }
        }
    }

}
