package net.laboulangerie.laboulangeriecore.commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class SpeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        Player player = (Player) sender;
        YamlConfiguration data = UsersData.getOrCreate(player);
        Boolean isEnabled = data.getBoolean("enable-speed-path", true);
        data.set("enable-speed-path", !isEnabled);
        try {
            UsersData.save((Player) sender, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage(Component
                .text(String.format("Vous avez %s la vitesse sur les chemins !",
                        isEnabled ? "désactivé" : "activé"))
                .color(NamedTextColor.GREEN));
        return true;
    }
}
