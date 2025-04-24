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

public class WNarrationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length == 0) { // Vérification des arguments
            sender.sendMessage(Component.text("Vous devez spécifier une narration. (/" + cmd.getName() + " [narration])", NamedTextColor.DARK_RED));
            return true;
        }

        String narration = "[Narration]";

        for(String arg : args) { // Création du texte
            narration += " " + arg;
        }
        

        for (Player targetInServer : Bukkit.getOnlinePlayers()){ // Envoie du résultat à tous les joueurs
            targetInServer.sendMessage(
                Component.text(
                    narration
                ).color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD)
            );

            targetInServer.playSound(targetInServer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        return true;
    }
}