package net.laboulangerie.laboulangeriecore.moreroleplay;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NarrationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length == 0) { // Vérification des arguments
            sender.sendMessage(Component.text("Vous devez spécifier une narration. (/" + cmd.getName() + " [narration])", NamedTextColor.DARK_RED));
            return true;
        }
        
        Player player = (Player) sender;
        String narration = "[Narration]";

        for(String arg : args) { // Création du texte
            narration += " " + arg;
        }

        for (Player targetInRadius : Bukkit.getOnlinePlayers()){ // Envoie du résultat à tous les joueurs
            if (targetInRadius.getWorld().equals(player.getWorld()) && targetInRadius.getLocation().distance(player.getLocation()) <= LaBoulangerieCore.PLUGIN.getConfig().getInt("rp-radius", 10)){  
                // ... aux joueurs dans le radius.
                targetInRadius.sendMessage(
                    Component.text(
                        narration
                    ).color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD)
                );
                
                targetInRadius.playSound(targetInRadius.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }
        
        return true;
    }
}