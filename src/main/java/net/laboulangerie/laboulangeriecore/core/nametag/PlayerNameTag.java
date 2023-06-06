package net.laboulangerie.laboulangeriecore.core.nametag;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.core.UsersData;

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
        entity.destroy(viewer);
    }

    private void createNameTags() {
        entity = new NameTagEntity(player.getLocation(), 1000000, player.getEntityId()); // TODO change that
        for (Player viewer : viewers) entity.spawn(viewer);
    }

    private void sendEntities(Player viewer) {
        entity.spawn(viewer);
    }

    public void updatePosition() {
        if (!entity.shouldBeDisplayed()) return;
        // for (Player viewer : viewers) {
        //     NMSEntityTeleport.send(
        //         viewer, entity, player.getLocation().getX(),
        //         player.getBoundingBox().getMaxY() + 0.3, player.getLocation().getZ()
        //     );
        // }
    }

    /**
     * Send metadata of the different entities composing the name tag to the viewers (other players)
     * 
     * @param isVisible
     */
    public void updateState() {
        entity.setCrouching(player.isSneaking());
        entity.setVisible(!player.isInvisible());

        if (!entity.shouldBeDisplayed()) return;
        entity.sendMetadata(viewers.toArray(new Player[viewers.size()]));
    }

    public void updateText() {
        Component component = Component.text("sssdsdsdsd");
        NameTagManager.rawNameTags.forEach(line -> {
            component.append(
                renderer.getPapiMiniMessage(player).deserialize(line)
            );
            component.appendNewline();
        });

        entity.setText(component);

        if (!entity.shouldBeDisplayed()) return;

        entity.sendMetadata(viewers.toArray(new Player[viewers.size()]));
    }

    public void destroy() {
        entity.destroy(viewers.toArray(new Player[viewers.size()]));
        for (Player viewer : viewers) {
            if (viewer == player) continue;
            PlayerNameTag viewerNameTag = PlayerNameTag.get(viewer);
            if (viewerNameTag != null) viewerNameTag.removeViewer(player);
        }
    }

    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
            if (p.player.getUniqueId().equals(player.getUniqueId())) return (p);
        return (null);
    }
}
