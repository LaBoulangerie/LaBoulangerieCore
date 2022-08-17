package net.laboulangerie.laboulangeriecore.nametag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityMountEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        new PlayerNameTag(event.getPlayer());//.addViewer(event.getPlayer());
        NameTagManager.idToPlayer.put(event.getPlayer().getEntityId(), event.getPlayer());
    }

    @EventHandler
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerNameTag nameTag = PlayerNameTag.get(player);
        if (nameTag == null) return;

        new BukkitRunnable() { //Event is fired before the effect is applied
            @Override          //thus we wait 2 ticks before updating the name tag
            public void run() { nameTag.updateState(); }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        PlayerNameTag nameTag = PlayerNameTag.get(event.getPlayer());
        if (nameTag == null) return;

        nameTag.updatePosition();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PlayerNameTag playerNameTag = PlayerNameTag.get(event.getPlayer());
        if (playerNameTag == null) return;

        playerNameTag.updatePosition();
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        new BukkitRunnable() { //Event is fired before the player is actually sneaking, so its bounding box
            @Override          //is still standing, thus we wait 2 ticks before updating the nametag
            public void run() {
                PlayerNameTag.get(event.getPlayer()).updateState();
                PlayerNameTag.get(event.getPlayer()).updatePosition();
            }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerNameTag nameTag = PlayerNameTag.get(event.getPlayer());
        if (nameTag != null) nameTag.destroy();
        NameTagManager.idToPlayer.remove(event.getPlayer().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMountEntity(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        PlayerNameTag nameTag = PlayerNameTag.get((Player) event.getEntity());
        if (nameTag != null) nameTag.updatePosition();
    }
}
