package net.laboulangerie.laboulangeriecore.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraGenRemover implements Listener {

    private List<Chunk> newChunks;

    public ElytraGenRemover() {
        this.newChunks = new ArrayList<Chunk>();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk())
            this.newChunks.add(event.getChunk());
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {

        Chunk chunk = event.getChunk();
        boolean isNewChunk = false;

        for (Chunk newChunk : newChunks) {
            if (newChunk.getX() == chunk.getX() && newChunk.getZ() == chunk.getZ()) {
                isNewChunk = true;
            }
        }

        if (!isNewChunk)
            return;

        newChunks.remove(event.getChunk());

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
}
