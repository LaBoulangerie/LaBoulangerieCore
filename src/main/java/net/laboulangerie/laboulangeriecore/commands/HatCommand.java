package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Vous devez être un joueur pour faire /hat.");
            return true;
        }

        Player player = (Player) sender;
        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            player.sendMessage("Vous devez tenir un item dans votre main pour faire /hat.");
            return true;
        }

        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack item = player.getInventory().getItemInMainHand();

        player.getInventory().setHelmet(item);
        player.getInventory().setItemInMainHand(helmet);

        return true;
    }

}
