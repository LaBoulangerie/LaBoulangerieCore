package net.laboulangerie.laboulangeriecore.moreroleplay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class RollCommand implements CommandExecutor {
    private HashMap<UUID,Long> commandCooldown;

    public RollCommand() {
        this.commandCooldown = new HashMap<>();
        
        LaBoulangerieCore.PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(LaBoulangerieCore.PLUGIN, () -> {
            // Nettoyage de rollCooldown
            for (Map.Entry<UUID,Long> entry : commandCooldown.entrySet()) {
                if ((System.currentTimeMillis() - entry.getValue()) > (LaBoulangerieCore.PLUGIN.getConfig().getDouble("rp-cooldown", 15) * 1000)) {
                    commandCooldown.remove(entry.getKey());
                }
            } 
        }, 72000, 72000);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { // Vérification de la nature du sender
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        long cooldown = System.currentTimeMillis() - commandCooldown.getOrDefault(player.getUniqueId(), (long) 0);

        if(cooldown < LaBoulangerieCore.PLUGIN.getConfig().getLong("rp-cooldown", 15) * 1000) { // Vérification du cooldown du sender
            sender.sendMessage(
                Component.text(
                    "Vous devez attendre " + LaBoulangerieCore.PLUGIN.getConfig().getInt("rp-cooldown", 15) + " seconde(s) entre chaque /roll."
                ).color(NamedTextColor.DARK_RED)
            );

            return true;
        } else {
            commandCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        }

        int max = args.length == 0 ? 20 : Integer.parseInt(args[0]);
        int result = MoreRoleplayUtil.getResult(sender, args, max);

        if (result == -1){
            return true;
        }

        for (Player targetInRadius : Bukkit.getOnlinePlayers()){ // Envoie du résultat ...
            YamlConfiguration targetData = UsersData.getOrCreate(targetInRadius);
            if (targetInRadius.equals(player)) {
                // ... au sender.
                Component message = Component.text( "[Roll] Vous avez jeté les dés de votre destin, et vous avez obtenu " + result + "/" + max + MoreRoleplayUtil.generateMessageByResult(result, max));
                targetInRadius.sendMessage(MoreRoleplayUtil.generateMessageColor(message, result, max));
            } else if (targetInRadius.getWorld().equals(player.getWorld()) && targetInRadius.getLocation().distance(player.getLocation()) <= LaBoulangerieCore.PLUGIN.getConfig().getInt("rp-radius", 10)){  
                // ... aux joueurs dans le radius.
                Component message = Component.text( "[Roll] " + PlainTextComponentSerializer.plainText().serialize(player.displayName()) + " a jeté les dés de son destin, et a obtenu " + result + "/" + max + MoreRoleplayUtil.generateMessageByResult(result, max) + " [" + (int)player.getLocation().distance(targetInRadius.getLocation()) + " bloc(s)]");
                targetInRadius.sendMessage(MoreRoleplayUtil.generateMessageColor(message, result, max));
            } else if (targetData.getBoolean("enable-spy-roll")) {
                // ... aux admins avec le spyroll actif.
                targetInRadius.sendMessage(
                    Component.text(
                        "[Roll] " + PlainTextComponentSerializer.plainText().serialize(player.displayName()) + " a jeté les dés de son destin, et a obtenu " + result + "/" + max +
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
        
        return true;
    }
}