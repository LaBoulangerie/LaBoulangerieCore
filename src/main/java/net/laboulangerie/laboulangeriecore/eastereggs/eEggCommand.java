package net.laboulangerie.laboulangeriecore.eastereggs;

import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class eEggCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.console"));
            return true;
        }

        Player p = (Player) sender;
        if(args.length != 1) {
            p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.utilization"));
            return true;
        }
        if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")){
            if(p.hasPermission("eastereggs.add")) {
                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission"));
                return true;
            }

            Block block = p.getTargetBlockExact(5);

            if(block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) {
                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-head"));
                return true;
            }

            List<String> eggs = eEggFileUtil.eggsData.getStringList("eggs");
            eggs.add(eEggUtil.getBlockIdentifier(block));

            eEggFileUtil.eggsData.set("eggs", eggs);
            try {
                eEggFileUtil.eggsData.save(eEggFileUtil.eggsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.added"));
            return true;
        }

        if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
            if(!p.hasPermission("eastereggs.remove")) {
                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission"));
                return true;
            }

            Block block = p.getTargetBlockExact(5);
            if(block.getType() != Material.PLAYER_HEAD && block.getType() == Material.PLAYER_WALL_HEAD) {
                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-head"));
                return true;
            }

            String result = eEggUtil.getBlockIdentifier(block);
            List<String> eggs = eEggFileUtil.eggsData.getStringList("eggs");

            if (!eggs.contains(result)) {
                p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.not-existing"));
                return true;
            }

            eggs.remove(result);
            eEggFileUtil.eggsData.set("eggs", eggs);
            try {
                eEggFileUtil.eggsData.save(eEggFileUtil.eggsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.deleted"));
            return true;
            
        }
        if(args[0].equalsIgnoreCase("help")) {
            if(p.hasPermission("eastereggs.help")) {
                List<String> list = LaBoulangerieCore.PLUGIN.getConfig().getStringList("eastereggs.messages.help");
                for (int i = 0; i < list.size(); i++) {
                    p.sendMessage(list.get(i));
                }
            }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.permission"));
            return true;
        }

        p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.utilization"));
        return true;
    }
}
