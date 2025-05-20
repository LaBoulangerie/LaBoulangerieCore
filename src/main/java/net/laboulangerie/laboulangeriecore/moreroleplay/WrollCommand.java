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

public class WrollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        int max = args.length == 0 ? 20 : Integer.parseInt(args[0]);
        int result = MoreRoleplayUtil.getResult(sender, args, max);

        for (Player targetInServer : Bukkit.getOnlinePlayers()){ // Envoie du résultat à tous les joueurs
            targetInServer.sendMessage(
                Component.text(
                    "[Roll] Les dieux ont jeté les dés de vos destins, et ont obtenu " + result + "/" + max +
                    (result == max ? ", c'est une réussite critique !" : result == 1  ? ", c'est un échec critique !" : ".")
                ).color(result == max ? NamedTextColor.DARK_GREEN : result == 1 ? NamedTextColor.DARK_RED : NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD));

            targetInServer.playSound(targetInServer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        return true;
    }
}