package net.laboulangerie.laboulangeriecore.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ElytraGenRemover implements Listener {

    private List<String> newChunks;

    public ElytraGenRemover() {
        this.newChunks = new ArrayList<String>();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk() && event.getChunk().getWorld().getEnvironment() == Environment.THE_END)
            this.newChunks.add(getId(event.getChunk()));
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        if (!newChunks.contains(getId(event.getChunk()))) return;

        newChunks.remove(getId(event.getChunk()));

        for (Entity entity : event.getEntities()) {
            if (entity.getType() != EntityType.ITEM_FRAME) continue;

            ItemFrame frame = (ItemFrame) entity;
            if (frame.getItem().getType().equals(Material.ELYTRA)) {
                ItemStack specialBread = new ItemStack(Material.BREAD, 1);
                ItemMeta meta = specialBread.getItemMeta();
                meta.setCustomModelData(1);
                specialBread.setItemMeta(meta);
                frame.setItem(specialBread);
                break;
            }
        }
    }

    private String getId(Chunk chunk) {
        return chunk.getWorld().getName() + " " + chunk.getX() + " " + chunk.getZ();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenEnderChest(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST
                && event.getAction() == Action.RIGHT_CLICK_BLOCK)
            event.setCancelled(true);
    }
}
