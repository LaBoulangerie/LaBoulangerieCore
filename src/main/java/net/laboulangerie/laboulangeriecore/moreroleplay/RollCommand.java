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

public class RollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de l'existence du joueur
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        int max = 100;
        int result;
        string message;

        if (args.length == 0) { // Détermination du maximum (si renseigné)
            int result = random.nextInt(100) + 1;
        } else {
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Merci d'indiquer un nombre entier en tant que maximum.", NamedTextColor.DARK_RED));
                return true;
            }

            if (max != 0){
                result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage(Component.text("0 n'est pas un maximum valide.", NamedTextColor.DARK_RED));
                return true;
            }
        }

        // Envoie du résultat aux joueurs proches
        Location playerLocation = sender.getLocation();
        int radius = LaBoulangerieCore.PLUGIN.getConfig().getInt("roll-radius", 10);

        for (Player target : player.getWorld().getPlayers()) {
            if (target.getLocation().distance(playerLocation) <= radius) {
                if(result == max)
                    message = sender.displayName() + " a jeté les dés de son destin, et a obtenu " + result + "/" + max + ", c'est une réussite critique !";
                else if(result == 1)
                    message = sender.displayName() + " a jeté les dés de son destin, et a obtenu " + result + "/" + max + ", c'est un échec critique !";
                else
                    message = sender.displayName() + " a jeté les dés de son destin, et a obtenu " + result + "/" + max + ".";

                target.sendMessage(Component.text(message)
                    .color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD));
            }
        }
        
        return true;
    }
}
