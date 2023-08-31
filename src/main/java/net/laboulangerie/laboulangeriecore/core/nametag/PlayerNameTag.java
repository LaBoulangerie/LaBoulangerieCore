package net.laboulangerie.laboulangeriecore.core.nametag;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        createNameTag();
        updateText();

        YamlConfiguration playerData = UsersData.getOrCreate(owner);
        if (playerData.getBoolean("show-own-name-tag", false))
            addViewer(owner);
    }

    public Player getOwner() {
        return player;
    }

    public List<Player> getViewers() {
        return viewers;
    }

    public void addViewer(Player viewer) {
        if (viewers.contains(viewer))
            return;
        sendEntities(viewer);
        viewers.add(viewer);
    }

    public void removeViewer(Player viewer) {
        viewers.remove(viewer);
        entity.destroy(viewer);
    }

    private void createNameTag() {
        entity = new NameTagEntity(player.getLocation(), NameTagManager.nextId(), player.getEntityId());
        for (Player viewer : viewers)
            entity.spawn(viewer);
    }

    private void sendEntities(Player viewer) {
        entity.spawn(viewer);
    }

    /**
     * Send metadata of the different entities composing the name tag to the viewers
     * (other players)
     * 
     * @param isVisible
     */
    public void updateState() {
        entity.setCrouching(player.isSneaking());
        entity.setVisible(!player.isInvisible());

        if (!entity.shouldBeDisplayed())
            return;
        entity.sendMetadata(viewers.toArray(new Player[viewers.size()]));
    }

    public void updateText() {
        Component component = NameTagManager.rawNameTags.stream()
                .map(line -> renderer.getPapiMiniMessage(player).deserialize(line))
                .filter(line -> !PlainTextComponentSerializer.plainText().serialize(line).trim().equals(""))
                .reduce((arg0, arg1) -> arg0.appendNewline().append(arg1)).get();

        entity.setText(component.compact());

        if (!entity.shouldBeDisplayed())
            return;

        entity.sendMetadata(viewers.toArray(new Player[viewers.size()]));
    }

    public void destroy() {
        entity.destroy(viewers.toArray(new Player[viewers.size()]));
        for (Player viewer : viewers) {
            if (viewer == player)
                continue;
            PlayerNameTag viewerNameTag = PlayerNameTag.get(viewer);
            if (viewerNameTag != null)
                viewerNameTag.removeViewer(player);
        }
    }

    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
            if (p.player.getUniqueId().equals(player.getUniqueId()))
                return (p);
        return (null);
    }
}
