package net.laboulangerie.laboulangeriecore.houses;

import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.firstPos;
import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.secondPos;

import java.util.ArrayList;

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

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class CreateHouseCmd implements CommandExecutor {

    private void saveHouseToFile(@NotNull Player player, @NotNull String houseName, @NotNull ArrayList<Location> blocks) {
        final House house = new House(houseName);
        house.addBlocks(blocks);
        Location anchor = house.getBlocks().stream().reduce(new Location(player.getWorld(), 0, 0, 0), (e1, e2) -> e1.add(e2)).multiply((double) 1 / blocks.size());
        house.setAnchor(anchor);

        LaBoulangerieCore.housesManager.addHouse(house);

        try {
            LaBoulangerieCore.housesManager.saveHouses();
        } catch(Exception e) {
            e.printStackTrace();
            player.sendMessage("§4An error occurred when trying to save the house");
            return;
        }

        player.sendMessage("§aHouse successfully saved!");
    }

    private void scanSponges(@NotNull Player player, @NotNull String houseName) {
        final World world = firstPos.getWorld();
        final int xMin = Integer.min(firstPos.getBlockX(), secondPos.getBlockX());
        final int xMax = Integer.max(firstPos.getBlockX(), secondPos.getBlockX());
        final int yMin = Integer.min(firstPos.getBlockY(), secondPos.getBlockY());
        final int yMax = Integer.max(firstPos.getBlockY(), secondPos.getBlockY());
        final int zMin = Integer.min(firstPos.getBlockZ(), secondPos.getBlockZ());
        final int zMax = Integer.max(firstPos.getBlockZ(), secondPos.getBlockZ());

        ArrayList<Location> blocks = new ArrayList<>();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    final Location location = new Location(world, x, y, z);
                    final Block block = world.getBlockAt(location);
                    if (block.getType().equals(Material.AIR)) continue;

                    if (block.getType().equals(Material.SPONGE)) {
                        blocks.add(location);
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        saveHouseToFile(player, houseName, blocks);
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
        args[0] = args[0].replaceAll("_", " ");
        if (LaBoulangerieCore.housesManager.getHouseByName(args[0]).isPresent()) {
            sender.sendMessage("§4This house already exist! Please delete it first");
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                scanSponges((Player) sender, args[0]);
            }
        }.runTask(LaBoulangerieCore.PLUGIN);
        return false;
    }
}
