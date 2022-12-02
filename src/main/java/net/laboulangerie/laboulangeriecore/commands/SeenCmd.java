package net.laboulangerie.laboulangeriecore.commands;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriecore.core.UsersData;

public class SeenCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0) return false;
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player.isOnline()) {
            sender.sendMessage(
                "§ePlayer §b" + player.getName() + "§e is §aonline§e,\nIP address: §a"
                + ((Player) player).getAddress().getHostString()
                + "§e,\nUUID: §b" + player.getUniqueId().toString());
        }else {
            YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
            sender.sendMessage("§ePlayer §b" + player.getName() + "§e is §4offline\n§eIP address: §a"
            + data.getString("last-ip-address", "§4Unknown")
            + "\n§eUUID: §b" + player.getUniqueId().toString()
            + "\n§eLast connection: §7" + new Date(player.getLastSeen()).toString());
        }
        return true;
    }
}
