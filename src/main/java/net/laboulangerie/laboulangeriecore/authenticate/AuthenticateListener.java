package net.laboulangerie.laboulangeriecore.authenticate;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuthenticateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onInventoryClickWithAuthenticate(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        int resultSlot = inventory.getSize() - 1;
        switch (inventory.getType()) {
            case WORKBENCH:
            case CRAFTING:
                resultSlot = 0;
                break;
            case CARTOGRAPHY:
            case ENCHANTING:
            case ANVIL:
                break;
            default:
                return;
        }

        if (!containsAuthenticate(inventory)
                || !isAuthenticated(event.getCurrentItem()))
            return;

        inventory.setItem(resultSlot, new ItemStack(Material.AIR));
    }

    private boolean containsAuthenticate(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        boolean containsAuthenticated = false;

        for (int i = 0; i < items.length; i++) {
            if (isAuthenticated(items[i])) {
                containsAuthenticated = true;
                break;
            }
        }
        return containsAuthenticated;
    }

    private boolean isAuthenticated(ItemStack item) {
        return item != null && new Authenticable(item).isAuthenticated();
    }
}
