package net.laboulangerie.laboulangeriecore.nametag;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.lang.reflect.Method;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().forEach(p -> {
            LaBoulangerieCore.PLUGIN.getNameTagManager().updateNameTag(p);
        });

        final PlayerNameTag playerNameTag = new PlayerNameTag(player);
        PlayerNameTag.nameTags.add(playerNameTag);

        LaBoulangerieCore.PLUGIN.getNameTagManager().updateNameTag(player);
    }

    private void toggleTextSneak(boolean isSneaking, NMSEntities entity) {
        try {
            final Method setShiftKeyDown = entity.getEntity().getClass().getMethod("f", boolean.class);
            setShiftKeyDown.invoke(entity.getEntity(), isSneaking);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveNameTag(Player player, boolean sneak) {
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag == null) return;

        final Location location = player.getLocation();

        final double sneakHeight = sneak ? 0.4 : 0;

        final NMSEntities above = playerNameTag.getAbove();
        final NMSEntities nameTagAbove = playerNameTag.getNameTag();
        final NMSEntities below = playerNameTag.getBelow();

        toggleTextSneak(sneak, above);
        toggleTextSneak(sneak, nameTagAbove);
        toggleTextSneak(sneak, below);

        Bukkit.getOnlinePlayers().forEach(p -> {
            NMSEntityMetadata.send(p, above);
            NMSEntityMetadata.send(p, nameTagAbove);
            NMSEntityMetadata.send(p, below);
            NMSEntityTeleport.send(p, above, location.getX(), location.getY() + 2.4 - sneakHeight, location.getZ());
            NMSEntityTeleport.send(p, nameTagAbove, location.getX(), location.getY() + 2.1 - sneakHeight, location.getZ());
            NMSEntityTeleport.send(p, below, location.getX(), location.getY() + 1.8 - sneakHeight, location.getZ());
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        moveNameTag(event.getPlayer(), event.getPlayer().isSneaking());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        moveNameTag(event.getPlayer(), event.isSneaking());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag != null) {
            PlayerNameTag.nameTags.remove(playerNameTag);

            Bukkit.getOnlinePlayers().forEach(p -> {
                NMSEntityDestroy.send(p, playerNameTag.getAbove().getID());
                NMSEntityDestroy.send(p, playerNameTag.getNameTag().getID());
                NMSEntityDestroy.send(p, playerNameTag.getBelow().getID());
            });
        }
    }
}
