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

public class WrollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de l'existence du joueur
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        int max = 100;
        int result;

        if (args.length == 0) { // Détermination du maximum (si renseigné)
            result = random.nextInt(100) + 1;
        } else {
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4Merci d'indiquer un nombre entier en tant que maximum.");
                return true;
            }

            if (max != 0){
                result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage("§40 n'est pas un maximum valide.");
                return true;
            }
        }

        // Envoie du résultat aux joueurs
        String message;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if(result == max)
                message = "Les dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max + ", c'est une réussite critique !";
            else if(result == 1)
                message = "Les dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max + ", c'est un échec critique !";
            else
                message = "Les dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max + ".";

            target.sendMessage(Component.text(message, result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW));
        }
        
        return true;
    }
}
