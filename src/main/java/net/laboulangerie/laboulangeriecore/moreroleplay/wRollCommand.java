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

public class MoreRolePlayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        if (args != null) {
            int result = random.nextInt(100) + 1;
        } else if {
            try {
                int max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4Merci d'indiquer un nombre entier en tant que maximum.");
            }

            if (max != 0){
                int result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage("§40 n'est pas un maximum valide.");
                return true;
            }
        }

        for (Player target : player.getWorld().getPlayers()) {
            if(result == max)
                target.sendMessage("&2&lLes dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max +", c'est une réussite critique !");
            else if(result == 1)
                target.sendMessage("&4&lLes dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max +", c'est un échec critique !");
            else
                target.sendMessage("&e&lLes dieux ont jeté les dés de votre destin, et ont obtenu " + result + "/" + max +".");
            
            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        return true;
    }
}
