package net.laboulangerie.laboulangeriecore.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;

public class CoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1) return false;

        if (Arrays.asList("reload", "rl").contains(args[0].toString())) {
            sender.sendMessage("§bReloading config...");
            LaBoulangerieCore.PLUGIN.reloadConfig();
            sender.sendMessage("§bReloading name tags...");
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("conversion")) {
            if (!(sender instanceof Player))
                sender.sendMessage("§4Only players can use that");
            else
                ConversionInv.displayConversionInv((Player) sender);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        
        return null;
    }
}