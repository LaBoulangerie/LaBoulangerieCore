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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.firstPos;
import static net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener.secondPos;

public class ListHouseCmd implements CommandExecutor {

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

        if (!LaBoulangerieCore.PLUGIN.housesManager.getDataFolder().exists()) {
            sender.sendMessage("§4There is no house!");
            return false;
        }

        if (LaBoulangerieCore.PLUGIN.housesManager.getHouses().size() == 0) {
            sender.sendMessage("§4There is no house!");
            return false;
        }

        sender.sendMessage("§6====================");
        sender.sendMessage("§aHouses: "+LaBoulangerieCore.PLUGIN.housesManager.getHouses().size());
        for (House house : LaBoulangerieCore.PLUGIN.housesManager.getHouses().values()) {
            sender.sendMessage("§e"+house.getName()+", "+house.getUUID());
        }
        sender.sendMessage("§6====================");

        return false;
    }
}
