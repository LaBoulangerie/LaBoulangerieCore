package net.laboulangerie.laboulangeriecore.houses;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.firstPos;
import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.secondPos;

public class CreateHouseCmd implements CommandExecutor {

    private void saveHouseToFile(@NotNull CommandSender sender, @NotNull String houseName, @NotNull List<Location> blocks) {
        final House house = new House(houseName);
        house.addBlocks(blocks.stream().toList());

        LaBoulangerieCore.PLUGIN.housesManager.addHouse(house);

        try {
            LaBoulangerieCore.PLUGIN.housesManager.saveHouses();
        } catch(Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage("§aHouse successfully saved!");
    }

    private void scanSponges(@NotNull CommandSender sender, @NotNull String houseName) {
        final World world = firstPos.getWorld();
        final int xMin = Integer.min(firstPos.getBlockX(), secondPos.getBlockX());
        final int xMax = Integer.max(firstPos.getBlockX(), secondPos.getBlockX());
        final int yMin = Integer.min(firstPos.getBlockY(), secondPos.getBlockY());
        final int yMax = Integer.max(firstPos.getBlockY(), secondPos.getBlockY());
        final int zMin = Integer.min(firstPos.getBlockZ(), secondPos.getBlockZ());
        final int zMax = Integer.max(firstPos.getBlockZ(), secondPos.getBlockZ());

        List<Location> blocks = new ArrayList<>();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    final Location location = new Location(world, x, y, z);
                    final Block block = world.getBlockAt(location);
                    if (block.getType().equals(Material.AIR)) continue;

                    if (block.getType().equals(Material.SPONGE)) {
                        blocks.add(location);
                    }
                }
            }
        }
        saveHouseToFile(sender, houseName, blocks);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§4Invalid usage try createhouse <name>");
            return false;
        }

        if (firstPos == null || secondPos == null) {
            sender.sendMessage("§4You must select a valid area using the house wand first!");
            return false;
        }

        if (LaBoulangerieCore.PLUGIN.housesManager.getHouseByName(args[0]).isPresent()) {
            sender.sendMessage("§4This house already exist! Please delete it first");
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                scanSponges(sender, args[0]);
            }
        }.runTaskAsynchronously(LaBoulangerieCore.PLUGIN);
        return false;
    }
}
