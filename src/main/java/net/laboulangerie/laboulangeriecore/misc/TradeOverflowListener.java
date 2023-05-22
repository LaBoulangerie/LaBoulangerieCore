package net.laboulangerie.laboulangeriecore.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class TradeOverflowListener implements Listener {
    private List<Player> potentialDroppers;

    public TradeOverflowListener() {
        this.potentialDroppers = new ArrayList<Player>();
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.MERCHANT) return;
        
        Player player = (Player) event.getPlayer();
        potentialDroppers.add(player);
        // Remove the potential dropper one tick later, only tick where the player can drop something
        Bukkit.getScheduler().runTaskLater(LaBoulangerieCore.PLUGIN, () -> potentialDroppers.remove(player), 1);
    }

    @EventHandler
    void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!event.isCancelled() || !potentialDroppers.contains(player)) return;
        
        ItemStack itemStack = event.getItemDrop().getItemStack();
        player.getWorld().dropItem(player.getLocation(), itemStack);
    }
}
