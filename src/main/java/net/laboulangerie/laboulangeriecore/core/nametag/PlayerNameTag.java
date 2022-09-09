package net.laboulangerie.laboulangeriecore.core.nametag;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityDestroy;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityTeleport;

public class PlayerNameTag {

    public static List<PlayerNameTag> nameTags = new ArrayList<>();

    private List<Player> viewers;
    private List<ArmorStandEntity> nameTagEntities = new ArrayList<>();
    private Player player;
    private ComponentRenderer renderer;

    public PlayerNameTag(Player owner) {
        this.renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();
        this.viewers = new ArrayList<>();
        this.player = owner;
        nameTags.add(this);
        createNameTags();
        updateText();

        YamlConfiguration playerData = UsersData.get(owner).orElseGet(() -> UsersData.createUserData(owner));
        if (playerData.getBoolean("show-own-name-tag", false)) addViewer(owner);
    }

    public Player getOwner() { return player; }
    public List<Player> getViewers() { return viewers; }

    public void addViewer(Player viewer) {
        if (viewers.contains(viewer)) return;
        sendEntities(viewer);
        viewers.add(viewer);
    }

    public void removeViewer(Player viewer) {
        viewers.remove(viewer);
        NMSEntityDestroy.send(viewer, nameTagEntities.stream().mapToInt(ArmorStandEntity::getID).toArray());
    }

    private void createNameTags() {
        for (int i = 0; i < NameTagManager.rawNameTags.size(); i++) {
            nameTagEntities.add(
                new ArmorStandEntity(player.getWorld(), ArmorStandEntity.EntityType.ARMOR_STAND,
                    player.getLocation().getX(), player.getBoundingBox().getMaxY() + 0.3 * i, player.getLocation().getZ()
                )
            );
            for (Player viewer : viewers) nameTagEntities.get(i).spawn(viewer);
        }        
    }

    private void sendEntities(Player viewer) {
        for (ArmorStandEntity armorStandEntity : nameTagEntities) armorStandEntity.spawn(viewer);
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
                Method setSneaking = entity.getEntity().getClass().getMethod("f", boolean.class);
                setSneaking.invoke(entity.getEntity(), player.isSneaking());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Method setCustomNameVisible = entity.getEntity().getClass().getMethod("n", boolean.class);
                setCustomNameVisible.invoke(entity.getEntity(), !player.isInvisible());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!entity.shouldBeDisplayed()) continue;
            for (Player viewer : viewers) entity.sendMetadata(viewer);
        }
    }

    public void updateText() {
        for (int i = 0; i < NameTagManager.rawNameTags.size(); i++) {
            ArmorStandEntity entity = nameTagEntities.get(i);
            Component component = renderer.getPapiMiniMessage(player).deserialize(NameTagManager.rawNameTags.get(i));
            entity.setText(component);
            
            if (!entity.shouldBeDisplayed()) continue;

            for (Player viewer : viewers) {
                entity.sendMetadata(viewer);
            }
        }
    }

    public void destroy() {
        for (Player viewer : viewers) {
            NMSEntityDestroy.send(viewer, nameTagEntities.stream().mapToInt(ArmorStandEntity::getID).toArray());
            if (viewer == player) continue;
            PlayerNameTag viewerNameTag = PlayerNameTag.get(viewer);
            if (viewerNameTag != null) viewerNameTag.removeViewer(player);
        };
    }

    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
            if (p.player.getUniqueId().equals(player.getUniqueId()))
                return (p);
        return (null);
    }
}
