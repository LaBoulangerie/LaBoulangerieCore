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
    private NameTagEntity entity;
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

    public Player getOwner() {
        return player;
    }

    public List<Player> getViewers() {
        return viewers;
    }

    public void addViewer(Player viewer) {
        if (viewers.contains(viewer)) return;
        sendEntities(viewer);
        viewers.add(viewer);
    }

    public void removeViewer(Player viewer) {
        viewers.remove(viewer);
        NMSEntityDestroy.send(viewer, entity.getID());
    }

    private void createNameTags() {
        entity = new NameTagEntity(player.getLocation(), 100);
        for (Player viewer : viewers) entity.spawn(viewer);
    }

    private void sendEntities(Player viewer) {
        entity.spawn(viewer);
    }

    public void updatePosition() {
        if (!entity.shouldBeDisplayed()) return;
        for (Player viewer : viewers) {
            NMSEntityTeleport.send(
                viewer, entity, player.getLocation().getX(),
                player.getBoundingBox().getMaxY() + 0.3, player.getLocation().getZ()
            );
        }
    }

    /**
     * Send metadata of the different entities composing the name tag to the viewers (other players)
     * 
     * @param isVisible
     */
    public void updateState() {
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
        if (!entity.shouldBeDisplayed()) return;
        for (Player viewer : viewers)
            entity.sendMetadata(viewer);
    }

    public void updateText() {
        Component component = Component.empty();
        NameTagManager.rawNameTags.forEach(line -> {
            component.append(
                renderer.getPapiMiniMessage(player).deserialize(line)
            );
            component.appendNewline();
        });

        entity.setText(component);

        if (!entity.shouldBeDisplayed()) return;

        for (Player viewer : viewers) {
            entity.sendMetadata(viewer);
        }
    }

    public void destroy() {
        for (Player viewer : viewers) {
            entity.destroy(viewer);
            if (viewer == player) continue;
            PlayerNameTag viewerNameTag = PlayerNameTag.get(viewer);
            if (viewerNameTag != null) viewerNameTag.removeViewer(player);
        } ;
    }

    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
            if (p.player.getUniqueId().equals(player.getUniqueId())) return (p);
        return (null);
    }
}
