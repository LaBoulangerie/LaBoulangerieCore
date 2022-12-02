package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class SpawnCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.teleport(LaBoulangerieCore.PLUGIN.getConfig().getLocation("spawn"));
            return true;
        }

        if (!args[0].equalsIgnoreCase("set")) return false;

        if (!player.hasPermission("laboulangeriecore.admin")) {
            player.sendMessage("!4You don't have permission to use this command!");
            return true;
        }

        LaBoulangerieCore.PLUGIN.getConfig().set("spawn", player.getLocation());
        LaBoulangerieCore.PLUGIN.saveConfig();
        player.sendMessage("§aSpawn set!");
        return true;
    }
}
