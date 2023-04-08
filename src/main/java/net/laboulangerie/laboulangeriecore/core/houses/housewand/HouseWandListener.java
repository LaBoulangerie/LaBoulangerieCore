package net.laboulangerie.laboulangeriecore.core.houses.housewand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class HouseWandListener implements Listener {

    public static Location firstPos;
    public static Location secondPos;

    private double getBlocksCount() {
        final int xMin = Integer.min(firstPos.getBlockX(), secondPos.getBlockX());
        final int xMax = Integer.max(firstPos.getBlockX(), secondPos.getBlockX());
        final int yMin = Integer.min(firstPos.getBlockY(), secondPos.getBlockY());
        final int yMax = Integer.max(firstPos.getBlockY(), secondPos.getBlockY());
        final int zMin = Integer.min(firstPos.getBlockZ(), secondPos.getBlockZ());
        final int zMax = Integer.max(firstPos.getBlockZ(), secondPos.getBlockZ());


        return (Math.abs(xMax - xMin) + 1) * (Math.abs(yMax - yMin) + 1) * (Math.abs(zMax - zMin) + 1);
    }

    private boolean isHoldingWand(@NotNull ItemStack item) {
        return (item.getType().equals(Material.IRON_AXE)
                && item.getItemMeta().hasDisplayName() && PlainTextComponentSerializer.plainText()
                        .serialize(item.getItemMeta().displayName()).equals("§6House wand")
                && item.getItemMeta().getCustomModelData() == 69420);
    }

    private @NotNull String generateMessage(@NotNull String index, @NotNull Location location) {
        final StringBuilder builder = new StringBuilder();
        builder.append("§d").append(index).append(" position set to (").append(location.getBlockX()).append(", ")
                .append(location.getBlockY()).append(", ").append(location.getBlockZ()).append(")");

        if (firstPos != null && secondPos != null) {
            builder.append(" (").append(getBlocksCount()).append(" blocks)");
        }

        return (builder.toString());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isHoldingWand(item)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isHoldingWand(item) || event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();
        if (block == null) return;

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            firstPos = block.getLocation();
            player.sendMessage(generateMessage("First", firstPos));
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            secondPos = block.getLocation();
            player.sendMessage(generateMessage("Second", secondPos));
        }
        event.setCancelled(true);
    }
}
