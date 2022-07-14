package net.laboulangerie.laboulangeriecore.eastereggs.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class eEggUtil {

    /**Here I get the amount of eastereggs*/
    public static Integer getMaxAmount() {
        List<String> list = eEggFileUtil.eggsData.getStringList("eggs");
        return list.size();
    }

    /**Here I get how much eastereggs the player have find*/
    public static Integer getPlayerAmount(Player p){
        File file = eEggFileUtil.getPlayerFile(p);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> list = config.getStringList("eggs");
        return list.size();

    }

    /**Here I get the prefix for thr messages*/
    public static String getPrefix() { return LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.prefix"); }

    /**Here I get all the gifts and I get a random integer for give a random gift to the player*/
    public static void giveGift(Player p){
        List<String> keys = new ArrayList<>();

        for(String key : LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("eastereggs.settings.gifts").getKeys(false)) {
            keys.add(key);
        }

        Random rdm = new Random();
        int i = rdm.nextInt(keys.size());

        if (LaBoulangerieCore.PLUGIN.getConfig().getBoolean("eastereggs.settings.gifts." + keys.get(i) + ".command") == true) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.settings.gifts." + keys.get(i) + ".gift").replace("%player%", p.getName()));
        } else {
            String[] item = LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.settings.gifts." + keys.get(i) + ".gift").split(":");
            ItemStack it = new ItemStack(Material.getMaterial(item[0]), Integer.parseInt(item[1]));
            p.getInventory().addItem(it);
        }
    }


    /**Here I prepare the message "validated"*/
    public static void sendValidation(Player p) {
        if(LaBoulangerieCore.PLUGIN.getConfig().getBoolean("settings.title") == true) {
            List<String> getTitle = LaBoulangerieCore.PLUGIN.getConfig().getStringList("messages.validated-title");
            List<String> title = new ArrayList<String>();
            for(String s : getTitle){
                title.add(s
                    .replace("%max-amount%", getMaxAmount().toString())
                    .replace("%amount%", getPlayerAmount(p).toString())
                );
            }
            p.sendTitle(title.get(0), title.get(1), 40, 20,20);
        }else p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.validated")
            .replace("%prefix%", getPrefix())
            .replace("%max-amount%", getMaxAmount().toString())
            .replace("%amount%", getPlayerAmount(p).toString())
        );
    }

    /**Here I prepare the message "already validated"*/
    public static void sendAlreadyValidated(Player p){
        if(LaBoulangerieCore.PLUGIN.getConfig().getBoolean("settings.title") == true) {
            List<String> getTitle = LaBoulangerieCore.PLUGIN.getConfig().getStringList("messages.already-validated-title");
            List<String> title = new ArrayList<String>();
            for(String s : getTitle){
                title.add(s);
            }
            p.sendTitle(title.get(0), title.get(1), 40, 20,20);
        }else p.sendMessage(
            LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.already-validated")
            .replace("%prefix%", getPrefix())
        );
    }
    public static String getBlockIdentifier(Block block) {
        return block.getWorld().getName() + "!" + block.getX() + "!" + block.getY() + "!" + block.getZ();
    }
}
