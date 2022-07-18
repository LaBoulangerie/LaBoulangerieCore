package net.laboulangerie.laboulangeriecore.nametag;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadNameTagCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("laboulangeriecore.nametag.admin")) {
            sender.sendMessage("§4You don't have the permission to use this command");
            return false;
        }

        LaBoulangerieCore.PLUGIN.reloadConfig();
        LaBoulangerieCore.PLUGIN.getNameTagManager().reload();

        sender.sendMessage("§aNameTag reloaded!");
        return true;
    }
}
