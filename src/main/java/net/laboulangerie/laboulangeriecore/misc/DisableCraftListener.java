package net.laboulangerie.laboulangeriecore.misc;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class DisableCraftListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCraftItem(CraftItemEvent event) {
        List<Material> disabledItems = getDisabledItems();
        Material resultMaterial = event.getRecipe().getResult().getType();
        System.out.println(resultMaterial);
        if (disabledItems.contains(resultMaterial)) {
            event.setCancelled(true);
            return;
        }
    }

    private List<Material> getDisabledItems() {
        return LaBoulangerieCore.PLUGIN.getConfig().getStringList("disabled-crafts").stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

}
