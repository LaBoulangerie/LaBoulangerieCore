package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class WrollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args){
        if (!(sender instanceof Player)) { // Vérification de la nature du sender
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        int max = 20;
		Random random = new Random();
        int result = (args.length == 0 ? random.nextInt(max) + 1 : null);

        try {
            result = random.nextInt(Integer.parseInt(args[0])) + 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Merci d'indiquer un nombre entier supérieur à 1 en tant que maximum.", NamedTextColor.DARK_RED));
            return true;
        }

        // Envoie du résultat aux joueurs proches
        for (Player target : Bukkit.getOnlinePlayers()){
            target.sendMessage(Component.text("Les dieux ont jeté les dés de vos destins, et ont obtenu " + result + "/" + max +
                (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : "."))
                .color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD));

                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        
        return true;
    }
}
