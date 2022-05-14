package net.laboulangerie.laboulangeriecore.houses.housewand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HouseWandListener implements Listener {

    public static Location firstPos;
    public static Location secondPos;

    private int getBlocksCount() {
        final int xMin = Integer.min(firstPos.getBlockX(), secondPos.getBlockX());
        final int xMax = Integer.max(firstPos.getBlockX(), secondPos.getBlockX());
        final int yMin = Integer.min(firstPos.getBlockY(), secondPos.getBlockY());
        final int yMax = Integer.max(firstPos.getBlockY(), secondPos.getBlockY());
        final int zMin = Integer.min(firstPos.getBlockZ(), secondPos.getBlockZ());
        final int zMax = Integer.max(firstPos.getBlockZ(), secondPos.getBlockZ());

        int count = 0;
        for (int i = xMin; i <= xMax ; count++,i++);
        for (int i = yMin; i <= yMax ; count++,i++);
        for (int i = zMin; i <= zMax ; count++,i++);

        return (count);
    }

    private boolean isHoldingWand(@NotNull ItemStack item) {
        return (item.getType().equals(Material.IRON_AXE) &&
                item.getItemMeta().getDisplayName().equals("§6House wand") &&
                item.getItemMeta().getCustomModelData() == 69420);
    }

    private @NotNull String generateMessage(@NotNull String index, @NotNull Location location) {
        final StringBuilder builder = new StringBuilder();
        builder.append("§d").append(index).append(" position set to (")
                .append(location.getBlockX()).append(", ")
                .append(location.getBlockY()).append(", ")
                .append(location.getBlockZ()).append(")");

        if (firstPos != null && secondPos != null) {
            builder.append("(").append(getBlocksCount()).append(")");
        }

        return (builder.toString());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isHoldingWand(item)) return;

        firstPos = event.getBlock().getLocation();

        player.sendMessage(generateMessage("First", firstPos));
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isHoldingWand(item)) return;

        final Block block = event.getClickedBlock();
        if (block == null) return;

        secondPos = block.getLocation();

        player.sendMessage(generateMessage("Second", secondPos));
        event.setCancelled(true);
    }
}
