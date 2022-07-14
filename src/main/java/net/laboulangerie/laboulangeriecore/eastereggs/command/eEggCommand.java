package net.laboulangerie.laboulangeriecore.eastereggs.command;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.eastereggs.Utils.eEggUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class eEggCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if(sender instanceof Player){
            Player p = (Player)sender;
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")){
                    if(p.hasPermission("eastereggs.add")){
                            if(p.getTargetBlockExact(5).getType() == Material.PLAYER_HEAD || p.getTargetBlockExact(5).getType() == Material.PLAYER_WALL_HEAD) {
                                int x = p.getTargetBlockExact(5).getX();
                                int y = p.getTargetBlockExact(5).getY();
                                int z = p.getTargetBlockExact(5).getZ();
                                String world = p.getWorld().getName();
                                List<String> list = LaBoulangerieCore.PLUGIN.getConfig().getStringList("eastereggs.eastereggs");
                                list.add(world + "!" + x + "!" + y + "!" + z);
                                LaBoulangerieCore.PLUGIN.getConfig().set("eastereggs.eastereggs", list);
                                LaBoulangerieCore.PLUGIN.saveConfig();
                                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.added").replace("%prefix%", eEggUtil.getPrefix()).replace("&", "§"));
                        }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-head").replace("%prefix%", eEggUtil.getPrefix()));
                    }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission").replace("%prefix%", eEggUtil.getPrefix()));

                } else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
                    if(p.hasPermission("eastereggs.remove")) {
                            if(p.getTargetBlockExact(5).getType() == Material.PLAYER_HEAD || p.getTargetBlockExact(5).getType() == Material.PLAYER_WALL_HEAD) {
                                int x = p.getTargetBlockExact(5).getX();
                                int y = p.getTargetBlockExact(5).getY();
                                int z = p.getTargetBlockExact(5).getZ();
                                String world = p.getWorld().getName();

                                String result = world + "!" + x + "!" + y + "!" + z;
                                List<String> list = LaBoulangerieCore.PLUGIN.getConfig().getStringList("eastereggs.eastereggs");

                                if (list.contains(result)) {
                                    list.remove(result);
                                    LaBoulangerieCore.PLUGIN.getConfig().set("eastereggs.eastereggs", list);
                                    LaBoulangerieCore.PLUGIN.saveConfig();
                                    p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.deleted").replace("%prefix%", eEggUtil.getPrefix()).replace("&", "§"));
                                }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-existing").replace("%prefix%", eEggUtil.getPrefix()).replace("&", "§"));
                        }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-head").replace("%prefix%", eEggUtil.getPrefix()));
                    }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission").replace("%prefix%", eEggUtil.getPrefix()));
                }else if(args[0].equalsIgnoreCase("amount")){
                    if(p.hasPermission("heastereggs.amount")){
                        p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.amount").replace("%prefix%", eEggUtil.getPrefix()).replace("%amount%", eEggUtil.getMaxAmount().toString()).replace("&", "§"));
                    }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission").replace("%prefix%", eEggUtil.getPrefix()).replace("&","§"));
                }else if(args[0].equalsIgnoreCase("help")){
                    if(p.hasPermission("heastereggs.help")){
                        List<String> list = LaBoulangerieCore.PLUGIN.getConfig().getStringList("eastereggs.messages.help");
                        for(int i = 0; i < list.size(); i++){
                            p.sendMessage(list.get(i).replace("&","§"));
                        }
                    }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission").replace("%prefix%", eEggUtil.getPrefix()));
                }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.utilisation").replace("%prefix%", eEggUtil.getPrefix()));
            }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.utilisation").replace("%prefix%", eEggUtil.getPrefix()));
        }else System.out.println(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.console"));

        return false;
    }
}
