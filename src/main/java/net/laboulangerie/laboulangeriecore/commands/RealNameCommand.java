package net.laboulangerie.laboulangeriecore.commands;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriecore.core.UsersData;

public class RealNameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        String nick = String.join(" ", args);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            YamlConfiguration userData = UsersData.getOrCreate(player);
            String playerNick = userData.getString("nick");

            if (playerNick != null && playerNick.equals(nick)) {
                sender.sendMessage(nick + " est joué par " + player.getName());
                return true;
            }
        }

        sender.sendMessage("Nick inconnu.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return null;
        }

        List<String> onlineNicks = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            YamlConfiguration userData = UsersData.getOrCreate(player);
            String nick = userData.getString("nick");

            if (nick != null) {
                onlineNicks.add(nick);
            }
        }

        return onlineNicks;
    }

}
