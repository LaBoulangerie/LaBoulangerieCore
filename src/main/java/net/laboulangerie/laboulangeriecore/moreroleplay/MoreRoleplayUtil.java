package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.Random;

import org.bukkit.command.CommandSender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MoreRoleplayUtil {
    public static int getResult(CommandSender sender, String[] args, int max) {
        int result;
		Random random = new Random();

        if (args.length == 0) { // Génération du résultat
            result = random.nextInt(max) + 1;
        } else {
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Le maximum doit être un nombre entier.", NamedTextColor.DARK_RED));
                return -1;
            }

            if (max > 1){
                result = random.nextInt(max) + 1;
            } else {
                sender.sendMessage(Component.text("Le maximum doit être supérieur à 1.", NamedTextColor.DARK_RED));
                return -1;
            }
        }

        return result;
    }

    public static String generateMessageByResult(int result, int max) {
        if(result == 1){
            return ", c'est un échec critique !";
        } else if (result == max){
            return ", c'est une réussite critique !";
        }
        return ".";
    }

    public static Component generateMessageColor(Component message, int result, int max) {
        if(result == 1){
            return message.color(NamedTextColor.DARK_RED);
        } else if (result == max){
            return message.color(NamedTextColor.DARK_GREEN);
        }
        return message.color(NamedTextColor.YELLOW);
    }
}
