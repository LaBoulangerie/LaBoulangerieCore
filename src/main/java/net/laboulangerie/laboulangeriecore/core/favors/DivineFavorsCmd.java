package net.laboulangerie.laboulangeriecore.core.favors;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DivineFavorsCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§4Only players can see their divine favors!");
                return true;
            }
            sender.sendMessage(
                "§bVous possédez §e"
                + DivineFavorsHolder.getDivineFavorsAmount((OfflinePlayer) sender)
                + "§b faveurs divines"
            );
            return true;
        }else if (!sender.hasPermission("laboulangeriecore.divinefavors.admin")) {
            sender.sendMessage("§4You don't have the permission to use this command");
            return false;

        }else if (args.length == 3 && Arrays.asList("give", "withdraw").contains(args[0])) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (target == null) {
                sender.sendMessage("§4Invalid player: "+ args[1]);
                return true;
            }
            double amount = 0;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4Unable to convert "+ args[2] + " to a decimal number!");
                return true;
            }
            if (args[0].equalsIgnoreCase("give")) {
                DivineFavorsHolder.giveDivineFavors(target, amount);
                sender.sendMessage("§bYou granted §e"+ args[2] +"§b divine favors to §e"+ args[1]);
            }else {
                if (!DivineFavorsHolder.withdrawDivineFavors(target, amount))
                    sender.sendMessage("§4Target "+ args[1] + " is too poor to withdraw " + args[2]);
                else
                    sender.sendMessage("§bYou subtracted §e"+ args[2] +"§b divine favors from §e"+ args[1]);
            }
            return true;
        }else {
            sender.sendMessage("§4Bad arguments!");
            sender.sendMessage("usage: /faveursdivines [give|withdraw] <player> <amount>");
            return true;
        }
    }
}
