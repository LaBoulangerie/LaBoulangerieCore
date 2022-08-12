package net.laboulangerie.laboulangeriecore.nametag;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityDestroy;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityMetadata;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityTeleport;
import net.laboulangerie.laboulangeriecore.nms.NMSSpawnEntityLiving;

public class PlayerNameTag {

    public static List<PlayerNameTag> nameTags = new ArrayList<>();

    private List<Player> viewers;
    private List<ArmorStandEntity> nameTagEntities = new ArrayList<>();
    private Player player;
    private ComponentRenderer renderer;

    public PlayerNameTag(@Nonnull Player owner) {
        this.renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();
        this.viewers = new ArrayList<>();
        this.player = owner;
        nameTags.add(this);
    }

    public @Nonnull Player getOwner() { return player; }
    public @Nonnull List<Player> getViewers() { return viewers; }

    public void createNameTags() {
        for (int i = 0; i < NameTagManager.rawNameTags.size(); i++) {
            byte index = (byte) nameTagEntities.size();
            nameTagEntities.set(index,
                new ArmorStandEntity(player.getWorld(), ArmorStandEntity.EntityType.ARMOR_STAND,
                    player.getLocation().getX(), player.getBoundingBox().getMaxY() + 0.3 * index, player.getLocation().getZ()
                )
            );
            for (Player viewer : viewers) spawnEntity(viewer, nameTagEntities.get(index));
        }        
    }

    public void updatePosition() {
        for (Player viewer : viewers)
            for (byte i = 0; i < nameTagEntities.size(); i++) {
                if (!nameTagEntities.get(i).shouldBeDisplayed()) continue;
                NMSEntityTeleport.send(viewer, nameTagEntities.get(i), player.getLocation().getX(), player.getBoundingBox().getMaxY() + 0.3 * i, player.getLocation().getZ());
            }
    }

    /**
     * Send metadata of the different entities composing the name tag to the viewers (other players)
     * @param isVisible
     */
    public void updateState() {
        for (int i = 0; i < nameTagEntities.size(); i++) {
            ArmorStandEntity entity = nameTagEntities.get(i);
            try {
                final Method setSneaking = entity.getEntity().getClass().getMethod("f", boolean.class);
                setSneaking.invoke(entity.getEntity(), player.isSneaking());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                final Method setCustomNameVisible = entity.getEntity().getClass().getMethod("n", boolean.class);
                setCustomNameVisible.invoke(entity.getEntity(), player.isInvisible());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!entity.shouldBeDisplayed()) continue;
            for (Player viewer : viewers) NMSEntityMetadata.send(viewer, entity);
        }
    }

    /**
     * Change the visibility of the name tag, does not send the update to the client,
     * {@link PlayerNameTag#updateState()} needs to be called for that to append
     * @param isVisible
     */
    public void setVisibility(boolean isVisible) {
        for (int i = 0; i < nameTagEntities.size(); i++) {
            ArmorStandEntity entity = nameTagEntities.get(i);
            try {
                final Method setCustomNameVisible = entity.getEntity().getClass().getMethod("n", boolean.class);
                setCustomNameVisible.invoke(entity.getEntity(), isVisible);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateText() {
        for (int i = 0; i < NameTagManager.rawNameTags.size(); i++) {
            ArmorStandEntity entity = nameTagEntities.get(i);
            Component component = renderer.getPapiMiniMessage(player).deserialize(NameTagManager.rawNameTags.get(i));
            entity.setText(component);
            
            if (!entity.shouldBeDisplayed()) return;
            
            for (Player viewer : viewers) {
                try {
                    NMSEntityMetadata.send(viewer, entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
        if (p.player.getUniqueId().equals(player.getUniqueId()))
        return (p);
        return (null);
    }

    public void destroy() {
        for (Player viewer : viewers) {
            NMSEntityDestroy.send(viewer, nameTagEntities.stream().mapToInt(ArmorStandEntity::getID).toArray());
        }
        nameTags.remove(this);
    }

    private void spawnEntity(Player target, ArmorStandEntity entity) {
        if (!entity.shouldBeDisplayed()) return;
        try {
            NMSSpawnEntityLiving.send(player, entity);
            NMSEntityMetadata.send(player, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
