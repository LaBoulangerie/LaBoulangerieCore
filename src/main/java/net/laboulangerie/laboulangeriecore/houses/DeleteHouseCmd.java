package net.laboulangerie.laboulangeriecore.houses;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DeleteHouseCmd implements CommandExecutor {

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

        final File file = new File("plugins/LaBoulangerieCore/housesSchema/" + args[0]);
        if (!file.exists()) {
            sender.sendMessage("§4This house doesn't exist!");
            return false;
        }
        file.delete();

        sender.sendMessage("§aHouse §e"+ args[0] +"§a deleted successfully");

        return false;
    }
}
