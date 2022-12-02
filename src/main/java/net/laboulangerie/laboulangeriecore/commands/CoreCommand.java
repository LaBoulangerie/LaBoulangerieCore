package net.laboulangerie.laboulangeriecore.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;

public class CoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1) return false;

        if (Arrays.asList("reload", "rl").contains(args[0].toString())) {
            sender.sendMessage("§bReloading config...");
            LaBoulangerieCore.PLUGIN.reloadConfig();
            UsersData.init(); // Clean cache & ensure directory exists
            sender.sendMessage("§bReloading name tags...");
            LaBoulangerieCore.PLUGIN.getNameTagManager().disable();
            LaBoulangerieCore.PLUGIN.getNameTagManager().enable();
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("conversion")) {
            if (!(sender instanceof Player))
                sender.sendMessage("§4Only players can use that");
            else {
                ConversionInv.displayConversionInv((Player) sender);
                YamlConfiguration data = UsersData.get((Player) sender).orElseGet(() -> UsersData.createUserData((Player) sender));
                data.set("conversions-count", data.getInt("conversions-count", 0)+1);
                try {
                    UsersData.save((Player) sender, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("nick") && args.length > 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (target == null) {
                sender.sendMessage("§4Player is unknown!");
                return true;
            }
            YamlConfiguration data = UsersData.get(target).orElseGet(() -> UsersData.createUserData(target));

            String name = List.of(args).subList(2, args.length).stream().reduce((a, b) -> a+" "+b).get();
            data.set("nick", name);
            try {
                UsersData.save(target, data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (target.isOnline()) {
                ((Player) target).displayName(Component.text(name));
            }
            sender.sendMessage("§aNickname set successfully!");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");
        if (args.length == 1) suggestions = Arrays.asList("reload", "rl", "conversion", "nick");
        if (args.length == 2 && args[0].equalsIgnoreCase("nick")) return null;
        
        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }
}
