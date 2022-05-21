package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class LinkCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        String link = LaBoulangerieCore.PLUGIN.getConfig().getString("links." + alias);
        sender.sendMessage(link);
        return true;
    }
}
