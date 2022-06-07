package net.laboulangerie.laboulangeriecore.advancements;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class AdvancementManager {
    public static boolean tryToCompleteAdvancement(Player player, String name) {
        if (!(AdvancementManager.playerHasAdvancement(player, name)) && AdvancementManager.isAdvancementEnabled(name)) {
            // name should be something like husbandry/break_diamond_hoe
            Advancement a = Bukkit.getAdvancement(new NamespacedKey("laboulangerie-datapack", name));
            if(a == null){
                return false;
            }
            NamespacedKey key = new NamespacedKey("laboulangerie-datapack", name);
            AdvancementProgress progress = player.getAdvancementProgress(Bukkit.getAdvancement(key));
            for(String criteria : progress.getRemainingCriteria())
                progress.awardCriteria(criteria);
            return true;
        }
        return false;
    }


    public static boolean playerHasAdvancement(Player player, String name) {
        // name should be something like husbandry/break_diamond_hoe
        Advancement a = Bukkit.getAdvancement(new NamespacedKey("laboulangerie-datapack", name));
        if(a == null) return false;
        AdvancementProgress progress = player.getAdvancementProgress(a);
        return progress.isDone();
    }

    public static boolean isAdvancementEnabled(String name) {
        // name should be something like husbandry/break_diamond_hoe
        name = name.replace("/", ".");
        name = name.substring(4);
        return LaBoulangerieCore.PLUGIN.getConfig().getBoolean("avancements." + name + ".enabled");
    }
}
