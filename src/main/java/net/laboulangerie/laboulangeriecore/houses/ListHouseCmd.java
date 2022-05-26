package net.laboulangerie.laboulangeriecore.houses;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class ListHouseCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Only players can use this command!");
            return true;
        }

        if (!LaBoulangerieCore.housesManager.getDataFolder().exists()) {
            sender.sendMessage("§4There is no house!");
            return false;
        }

        if (LaBoulangerieCore.housesManager.getHouses().size() == 0) {
            sender.sendMessage("§4There is no house!");
            return false;
        }

        sender.sendMessage("§6====================");
        sender.sendMessage("§aHouses: "+LaBoulangerieCore.housesManager.getHouses().size());
        for (House house : LaBoulangerieCore.housesManager.getHouses().values()) {
            sender.sendMessage("§e"+house.getName()+", "+house.getUUID());
        }
        sender.sendMessage("§6====================");

        return false;
    }
}
