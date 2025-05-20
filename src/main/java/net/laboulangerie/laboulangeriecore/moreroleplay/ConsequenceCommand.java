package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ConsequenceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length < 2) { // Vérification des arguments
            sender.sendMessage(Component.text("Vous devez spécifier un joueur et une conséquence. (/" + cmd.getName() + " [Joueur] [narration])", NamedTextColor.DARK_RED));
            return true;
        }

        Player player = null;

        try { // Tentative de récupération du joueur
            player = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            sender.sendMessage(Component.text("Le joueur " + args[0] + " n'est pas connecté ou reconnu.", NamedTextColor.DARK_RED));
            return true;
        }

        String narration = "[Narration] " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        player.sendMessage( // Envoie du message
            Component.text(
                narration
            ).color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.BOLD)
        );

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        return true;
    }
}