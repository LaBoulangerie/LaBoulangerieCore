package net.laboulangerie.laboulangeriecore.authenticate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;

import io.papermc.paper.event.player.PlayerLoomPatternSelectEvent;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;

public class AuthenticateListener implements Listener {

    // Anvil, Grindstone, Smithing
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPrepareResult(PrepareResultEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        if (containsAuthenticated(items)) {
            event.setResult(null);
            return;
        }
    }

    // Crafting table
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCraftItem(CraftItemEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (containsAuthenticated(matrix)) {
            event.setCancelled(true);
            return;
        }
    }

    // Loom
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onLoomPatternSelect(PlayerLoomPatternSelectEvent event) {
        ItemStack[] items = event.getLoomInventory().getContents();
        if (containsAuthenticated(items)) {
            event.setCancelled(true);
            return;
        }
    }

    // Stonecutter
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onStonecutterSelect(PlayerStonecutterRecipeSelectEvent event) {
        ItemStack[] items = event.getStonecutterInventory().getContents();
        if (containsAuthenticated(items)) {
            event.setCancelled(true);
            return;
        }
    }

    // Enchanting table
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEnchantItem(EnchantItemEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        if (containsAuthenticated(items)) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean containsAuthenticated(ItemStack[] items) {
        for (ItemStack item : items) {
            if (new Authenticable(item).isAuthenticated()) {
                return true;
            }
        }
        return false;
    }
}
