package net.laboulangerie.laboulangeriecore.moreroleplay;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class MeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de la nature du sender
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        if(args.length == 0) { // Vérification des arguments
            sender.sendMessage(Component.text("Vous devez spécifier une action. (/" + cmd.getName() + "[action])", NamedTextColor.DARK_RED));
            return true;
        }
        
        Player player = (Player) sender;
        String action = "[Action] ";

        for(String arg : args) { // Création du texte
            action += " " + arg;
        }

        for (Player targetInRadius : Bukkit.getOnlinePlayers()){ // Envoie de l'action ...
            YamlConfiguration targetData = UsersData.getOrCreate(targetInRadius);
            if (targetInRadius.getWorld().equals(player.getWorld()) && targetInRadius.getLocation().distance(player.getLocation()) <= LaBoulangerieCore.PLUGIN.getConfig().getInt("roll-radius", 10)){  
                // ... aux joueurs dans le radius.
                targetInRadius.sendMessage(
                    Component.text(
                        PlainTextComponentSerializer.plainText().serialize(player.displayName()) + action + (targetInRadius.equals(player) ? (" [" + (int)player.getLocation().distance(targetInRadius.getLocation()) + " bloc(s)]") : "")
                    ).color(NamedTextColor.YELLOW)
                );
            } else if (targetData.getBoolean("enable-spy-roll")) {
                // ... aux admins avec le spyroll actif.
                targetInRadius.sendMessage(
                    Component.text(
                        PlainTextComponentSerializer.plainText().serialize(player.displayName()) + action
                    ).color(NamedTextColor.YELLOW)
                    .append(Component.text(
                        " [" + player.getWorld().getName() + "(" + 
                        player.getLocation().getBlockX() + "," + 
                        player.getLocation().getBlockY() + "," + 
                        player.getLocation().getBlockZ() + ")" + "]"
                    ).hoverEvent(Component.text("Se téléporter à " + player.getName()))
                    .clickEvent(ClickEvent.runCommand("/tp " + player.getName())))
                    .color(NamedTextColor.YELLOW)
                );
            }
        }
        
        return true;
    }
}