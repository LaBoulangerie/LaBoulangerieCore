package net.laboulangerie.laboulangeriecore.xpbottle;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class XpBottleCreationListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isEnabled()) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.GLASS_BOTTLE) return;

        int maxReach = getMaxReach();
        Block target = player.getTargetBlockExact(maxReach);
        if (target != null && target.getType() != Material.AIR) return;

        int requiredXp = getXpAmount();
        if (getTotalExperience(player) < requiredXp) return;

        int lapisRequired = getLapisRequired();
        if (!hasItem(player, Material.LAPIS_LAZULI, lapisRequired)) return;

        // Execute creation
        removeExperience(player, requiredXp);
        removeItem(player, Material.LAPIS_LAZULI, lapisRequired);
        itemInHand.setAmount(itemInHand.getAmount() - 1);

        ItemStack xpBottle = createXpBottle(requiredXp);
        player.getInventory().addItem(xpBottle);

        event.setCancelled(true);
    }

    private ItemStack createXpBottle(int xpAmount) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = bottle.getItemMeta();
        String lorePrefix = LaBoulangerieCore.PLUGIN.getConfig()
            .getString("xp-bottle.lore-prefix", "xp: ");
        meta.lore(List.of(Component.text(lorePrefix + xpAmount)));
        bottle.setItemMeta(meta);
        return bottle;
    }

    private boolean hasItem(Player player, Material material, int amount) {
        return player.getInventory().contains(material, amount);
    }

    private void removeItem(Player player, Material material, int amount) {
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != material) continue;
            int itemAmount = item.getAmount();
            if (itemAmount <= remaining) {
                remaining -= itemAmount;
                item.setAmount(0);
            } else {
                item.setAmount(itemAmount - remaining);
                remaining = 0;
            }
            if (remaining <= 0) break;
        }
    }

    /**
     * Calculate total experience points from player's level and progress.
     * Uses Minecraft's XP formula.
     */
    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();

        int totalXp;
        if (level <= 16) {
            totalXp = (int) (level * level + 6 * level);
        } else if (level <= 31) {
            totalXp = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            totalXp = (int) (4.5 * level * level - 162.5 * level + 2220);
        }

        // Add progress towards next level
        totalXp += Math.round(progress * getXpForNextLevel(level));

        return totalXp;
    }

    /**
     * Get XP required for next level from current level.
     */
    private int getXpForNextLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    /**
     * Remove experience points from player.
     */
    private void removeExperience(Player player, int amount) {
        int currentTotal = getTotalExperience(player);
        int newTotal = Math.max(0, currentTotal - amount);
        setTotalExperience(player, newTotal);
    }

    /**
     * Set player's total experience points.
     */
    private void setTotalExperience(Player player, int totalXp) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        if (totalXp <= 0) return;

        // Calculate level from total XP
        int level = 0;
        int xpForLevel = 0;

        while (true) {
            int xpNeeded = getXpForNextLevel(level);
            if (xpForLevel + xpNeeded > totalXp) {
                break;
            }
            xpForLevel += xpNeeded;
            level++;
        }

        player.setLevel(level);
        int remainingXp = totalXp - xpForLevel;
        int xpForNext = getXpForNextLevel(level);
        float progress = xpForNext > 0 ? (float) remainingXp / xpForNext : 0;
        player.setExp(Math.min(progress, 0.99999f));
    }

    private boolean isEnabled() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getBoolean("xp-bottle.enabled", true);
    }

    private int getXpAmount() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getInt("xp-bottle.creation.xp-amount", 100);
    }

    private int getLapisRequired() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getInt("xp-bottle.creation.lapis-required", 1);
    }

    private int getMaxReach() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getInt("xp-bottle.creation.max-reach", 5);
    }
}
