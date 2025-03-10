package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class RollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de l'existence du joueur
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }

        int max = 20;
        int result;
		Random random = new Random();
        Player player = (Player) sender;

        if (args.length == 0) { // Détermination du maximum (si renseigné)
            result = random.nextInt(max) + 1;
        } else {
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Merci d'indiquer un nombre entier en tant que maximum.", NamedTextColor.DARK_RED));
                return true;
            }

            if (max > 1){
                result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage(Component.text("Merci d'indiquer un nombre supérieur à 1 en tant que maximum.", NamedTextColor.DARK_RED));
                return true;
            }
        }

        // Envoie du résultat aux joueurs proches
        Location playerLocation = player.getLocation();
        int radius = LaBoulangerieCore.PLUGIN.getConfig().getInt("roll-radius", 10);
        String message;

        for (Player target : player.getWorld().getPlayers()) {
            if (target.getLocation().distance(playerLocation) <= radius) {
                message = PlainTextComponentSerializer.plainText().serialize(player.displayName()) + " a jeté les dés de son destin, et a obtenu " + result + "/" + max +
                    (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".");

                target.sendMessage(Component.text(message)
                    .color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW));
            }
        }
        
        return true;
    }
}
