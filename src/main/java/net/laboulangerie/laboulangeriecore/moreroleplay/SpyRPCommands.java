package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class SpyRPCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de la nature du sender
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        Player player = (Player) sender;
        YamlConfiguration data = UsersData.getOrCreate(player);
        Boolean isEnabled = data.getBoolean("enable-spy-roll", false);
        data.set("enable-spy-roll", !isEnabled);

        try { // Sauvegarde du toggle
            UsersData.save((Player) sender, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage( // Envoie de la confirmation
                Component.text(String.format("Espionnage des /roll %s !",
                        isEnabled ? "désactivé" : "activé"))
                .color(NamedTextColor.GREEN));
        return true;
    }
}