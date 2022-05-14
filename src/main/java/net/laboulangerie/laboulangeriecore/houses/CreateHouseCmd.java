package net.laboulangerie.laboulangeriecore.houses;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.firstPos;
import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.secondPos;

public class CreateHouseCmd implements CommandExecutor {

    private void saveHouseTofile(@NotNull CommandSender sender, @NotNull String houseName, @NotNull Set<Location> locations) {
        final File file = new File("plugins/LaBoulangerieCore/houses/" + houseName);
        final StringBuilder builder = new StringBuilder();

        for (Location loc : locations) {
            builder.append(loc.getWorld()).append(", ")
                    .append(loc.getBlockX()).append(", ")
                    .append(loc.getBlockY()).append(", ")
                    .append(loc.getBlockZ()).append(System.lineSeparator());
        }

        try {
            Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            sender.sendMessage("§aHouse successfully saved under §bplugins/LaBoulangerieCore/houses/"+houseName+" §6("+locations.size()+")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanSponges(@NotNull CommandSender sender, @NotNull String houseName) {
        final World world = firstPos.getWorld();
        final int xMin = Integer.min(firstPos.getBlockX(), secondPos.getBlockX());
        final int xMax = Integer.max(firstPos.getBlockX(), secondPos.getBlockX());
        final int yMin = Integer.min(firstPos.getBlockY(), secondPos.getBlockY());
        final int yMax = Integer.max(firstPos.getBlockY(), secondPos.getBlockY());
        final int zMin = Integer.min(firstPos.getBlockZ(), secondPos.getBlockZ());
        final int zMax = Integer.max(firstPos.getBlockZ(), secondPos.getBlockZ());

        Set<Location> locations = new HashSet<>();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    final Location location = new Location(world, x, y, z);
                    final Block block = world.getBlockAt(location);
                    if (block.getType().equals(Material.AIR)) continue;

                    if (block.getType().equals(Material.SPONGE)) {
                        locations.add(location);
                    }
                }
            }
        }
        saveHouseTofile(sender, houseName, locations);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Only players can use this command!");
            return true;
        }
        if (!sender.hasPermission("laboulangeriecore.house.admin")) {
            sender.sendMessage("§4You don't have the permission to use this command");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§4Invalid usage try createhouse <name>");
            return false;
        }

        if (firstPos == null || secondPos == null) {
            sender.sendMessage("§4You must select a valid area using the house wand first!");
            return false;
        }

        if (new File("plugins/LaBoulangerieCore/houses/" + args[0]).exists()) {
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
