package net.laboulangerie.laboulangeriecore.misc;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeOverflowListener implements Listener {

    boolean isInventory(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    int numberOfEmptyStorageSlots(Inventory inventory) {
        int count = 0;
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null)
                count++;
        }
        return count;
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.MERCHANT)
            return;

        Player player = (Player) event.getPlayer();
        ItemStack[] storageContents = inventory.getStorageContents();

        int numberOfPlayerEmptySlots = numberOfEmptyStorageSlots(player.getInventory());
        int numberOfMerchantOccupiedSlots = inventory.getStorageContents().length
                - numberOfEmptyStorageSlots(inventory);

        if (numberOfPlayerEmptySlots >= numberOfMerchantOccupiedSlots)
            return;

        // Empty storage contents
        ItemStack[] airStacks = new ItemStack[storageContents.length];
        Arrays.fill(airStacks, new ItemStack(Material.AIR));
        inventory.setStorageContents(airStacks);

        // Then drop the original content
        for (ItemStack itemStack : storageContents) {
            if (itemStack != null)
                player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
}
