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
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;

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

        List<Entity> entities = event.getEntities();

        ItemFrame[] itemFrames = entities.stream().filter(e -> e.getType().equals(EntityType.ITEM_FRAME))
                .toArray(ItemFrame[]::new);

        for (ItemFrame itemFrame : itemFrames) {
            if (itemFrame.getItem().getType().equals(Material.ELYTRA)) {
                // TODO: make a more special item
                itemFrame.setItem(new ItemStack(Material.BREAD, 1));
            }
        }
    }
    private String getId(Chunk chunk) {
        return chunk.getWorld().getName() + " " + chunk.getX() + " " + chunk.getZ();
    }
}
