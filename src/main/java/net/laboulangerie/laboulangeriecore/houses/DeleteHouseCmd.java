package net.laboulangerie.laboulangeriecore.houses;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteHouseCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§4Invalid usage try deletehouse <name>");
            return false;
        }

        if (LaBoulangerieCore.housesManager.getHouseByName(args[0]).isEmpty()) {
            sender.sendMessage("§4This house doesn't exist!");
            return false;
        }
        House house = LaBoulangerieCore.housesManager.getHouseByName(args[0]).get();
        LaBoulangerieCore.housesManager.removeHouse(house.getUUID());
        LaBoulangerieCore.housesManager.deleteHouse(house.getUUID());

        sender.sendMessage("§aHouse §e"+ args[0] +"§a deleted successfully");

        return false;
    }
}
