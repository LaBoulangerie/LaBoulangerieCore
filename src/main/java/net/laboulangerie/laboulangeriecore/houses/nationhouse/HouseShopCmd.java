package net.laboulangerie.laboulangeriecore.houses.nationhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HouseShopCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }
        Player player = (Player) sender;

        HouseShop.displayShop(player, (short) 0);
        return true;
    }  
}
