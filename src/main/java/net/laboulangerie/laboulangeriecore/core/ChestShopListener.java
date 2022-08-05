package net.laboulangerie.laboulangeriecore.core;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gestern.gringotts.currency.Denomination;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.event.ShopPurchaseEvent;

import static org.gestern.gringotts.Configuration.CONF;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.maxgamer.quickshop.api.shop.Shop;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class ChestShopListener implements Listener {

    // In time
    // ShopPurchaseEvent -> EconomyCommitEvent -> ShopSuccessPurchaseEvent

    @EventHandler(priority = EventPriority.MONITOR)
    private void onShopPurchase(ShopPurchaseEvent event) {
        Shop shop = event.getShop();
        OfflinePlayer owner = Bukkit.getPlayer(shop.getOwner());
        Player purchaser = Bukkit.getPlayer(event.getPurchaser());
        Chest chest = (Chest) shop.getLocation().getBlock().getState();
        int total = (int) event.getTotal();

        List<ItemStack> stacks = getItemsForPrice(total);

        // If out of space
        if (isGoingToOverflow(chest.getInventory(), stacks)) {
            QuickShop.getInstance().text().of(purchaser, "purchase-out-of-space", owner.getName()).send();
            event.setCancelled(true);
            return;
        }
        // Withdraw purchaser
        LaBoulangerieCore.econ.withdrawPlayer(purchaser, total);

        // Add the corresponding items to the chest
        for (ItemStack stack : stacks) {
            chest.getInventory().addItem(stack);
        }

        // ""Cancel"" the transaction to the owner's inventory by setting total to 0
        // ! Must remove the price in QuickShop's messages in the config
        event.setTotal(0);

    }

    // Greedy algorithm to convert price to a list of items
    private List<ItemStack> getItemsForPrice(int total) {
        long remaining = total;
        List<ItemStack> items = new ArrayList<ItemStack>();

        for (Denomination denomination : CONF.getCurrency().getDenominations()) {
            long value = denomination.getValue();
            int count = 0;

            while (remaining > 0 && value <= remaining) {
                remaining -= value;
                count++;
            }

            if (count == 0)
                continue;

            ItemStack stack = new ItemStack(denomination.getKey().type);
            stack.setAmount(count);
            items.add(stack);
        }

        return items;
    }

    private boolean isGoingToOverflow(Inventory inventory, List<ItemStack> stacks) {
        // Empty slots are null
        int emptySlotsCount = (int) Stream.of(inventory.getContents()).filter(item -> item == null).count();

        // Keeping track of stacks that will fill a new slot
        int newSlotsCount = 0;

        for (ItemStack stack : stacks) {
            List<ItemStack> similars = similarItems(inventory, stack);

            if (similars.size() > 0) {
                int spaceRemaining = 0;

                for (ItemStack similar : similars) {
                    // Calculate space remaining in each slot of the same stack
                    spaceRemaining += (inventory.getMaxStackSize() - similar.getAmount());
                }

                // Calculate the amount left
                int amountLeft = stack.getAmount() - spaceRemaining;

                // If you can't squeeze the new stack in existing stacks of the same type
                if (amountLeft > 0)
                    // Then increase the count, new slot(s) will necesarily be taken
                    newSlotsCount += Math.ceil((double) amountLeft / inventory.getMaxStackSize());
            } else {
                newSlotsCount += Math.ceil((double) stack.getAmount() / inventory.getMaxStackSize());
            }
        }

        // If there are less empty slots than new filled slots
        if (emptySlotsCount < newSlotsCount)
            return true;

        // Else everything's fine, there are enough empty slots :)
        return false;

    }

    private List<ItemStack> similarItems(Inventory inventory, ItemStack item) {
        List<ItemStack> similars = new ArrayList<ItemStack>();

        for (ItemStack i : inventory.getContents()) {
            if (item.isSimilar(i)) {
                similars.add(i);
            }
        }

        return similars;
    }
}
