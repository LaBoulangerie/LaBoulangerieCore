package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class RollCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de la nature du sender
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }
        
        int max = 20;
        int result;
		Random random = new Random();
        Player player = (Player) sender;

        if (args.length == 0) { // Génération du résultat
            result = random.nextInt(max) + 1;
        } else {
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Le maximum doit être un nombre entier.", NamedTextColor.DARK_RED));
                return true;
            }

            if (max > 1){
                result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage(Component.text("Le maximum doit être supérieur à 1.", NamedTextColor.DARK_RED));
                return true;
            }
        }

        if (cmd.getName().equals("roll")){ // Envoie du résultat ...
            for (Player targetInRadius : Bukkit.getOnlinePlayers()){
                YamlConfiguration targetData = UsersData.getOrCreate(targetInRadius);
                if (targetInRadius.equals(player)) {
                    // ... au sender.
                    targetInRadius.sendMessage(
                        Component.text(
                            "Vous avez jeté les dés de votre destin, et vous avez obtenu " + result + "/" + max +
                            (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".")
                        ).color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                    );
                } else if (targetInRadius.getWorld().equals(player.getWorld()) && targetInRadius.getLocation().distance(player.getLocation()) <= LaBoulangerieCore.PLUGIN.getConfig().getInt("roll-radius", 10)){  
                    // ... aux joueurs dans le radius.
                    targetInRadius.sendMessage(
                        Component.text(
                            PlainTextComponentSerializer.plainText().serialize(player.displayName()) + " a jeté les dés de son destin, et a obtenu " + result + "/" + max +
                            (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".") + " [" + (int)player.getLocation().distance(targetInRadius.getLocation()) + " bloc(s)]"
                        ).color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                    );
                } else if (targetData.getBoolean("enable-spy-roll")) {
                    // ... aux admins avec le spyroll actif.
                    targetInRadius.sendMessage(
                        Component.text(
                            PlainTextComponentSerializer.plainText().serialize(player.displayName()) + " a jeté les dés de son destin, et a obtenu " + result + "/" + max +
                            (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".")
                        ).color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                        .append(Component.text(
                            " [" + player.getWorld().getName() + "(" + 
                            player.getLocation().getBlockX() + "," + 
                            player.getLocation().getBlockY() + "," + 
                            player.getLocation().getBlockZ() + ")" + "]"
                        ).hoverEvent(Component.text("Se téléporter à " + player.getName()))
                        .clickEvent(ClickEvent.runCommand("/tp " + player.getName())))
                        .color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                    );
                }
            }
        } else if (cmd.getName().equals("wroll")){ // Envoie du résultat à tous les joueurs
            for (Player targetInServer : Bukkit.getOnlinePlayers()){
                targetInServer.sendMessage(
                    Component.text(
                        "Les dieux ont jeté les dés de vos destins, et ont obtenu " + result + "/" + max +
                        (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".")
                    ).color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD));

                targetInServer.playSound(targetInServer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }
        
        return true;
    }
}