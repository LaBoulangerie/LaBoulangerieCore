package net.laboulangerie.laboulangeriecore.nametag;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityMountEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.nms.NMSEntities;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityDestroy;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityMetadata;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityTeleport;
import net.laboulangerie.laboulangeriecore.nms.NMSSpawnEntityLiving;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        new PlayerNameTag(event.getPlayer());
    }

    @EventHandler
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerNameTag nameTag = PlayerNameTag.get(player);
        if (nameTag == null) return;

        if (event.getAction().equals(EntityPotionEffectEvent.Action.ADDED) && event.getNewEffect() != null &&
                event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)
            ) {
            nameTag.setVisibility(false);

        } else if (event.getAction().equals(EntityPotionEffectEvent.Action.REMOVED) && event.getOldEffect() != null &&
                event.getOldEffect().getType().equals(PotionEffectType.INVISIBILITY)
            ) {
            nameTag.setVisibility(true);
        }
        nameTag.updateState(); // TODO: Test if this is enough and if setVisibility is really needed
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

        // for (PlayerNameTag tags : PlayerNameTag.nameTags) {
        //     if (tags == playerNameTag) continue;
        //     playerNameTag.getTagsVisible().putIfAbsent(tags.getNameTag().getID(), false);

        //     double distance = Math.pow(event.getPlayer().getLocation().getX() - tags.getPlayer().getLocation().getX(), 2) +
        //                     Math.pow(event.getPlayer().getLocation().getY() - tags.getPlayer().getLocation().getY(), 2) +
        //                     Math.pow(event.getPlayer().getLocation().getZ() - tags.getPlayer().getLocation().getZ(), 2);

        //     if (distance > 2000 && playerNameTag.getTagsVisible().get(tags.getNameTag().getID())) {
        //         NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getAbove().getID());
        //         NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getNameTag().getID());
        //         NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getBelow().getID());
        //         playerNameTag.getTagsVisible().put(tags.getNameTag().getID(), false);

        //         NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getAbove().getID());
        //         NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getNameTag().getID());
        //         NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getBelow().getID());
        //         tags.getTagsVisible().put(playerNameTag.getNameTag().getID(), false);
        //     }

        //     if (distance < 2000 && !playerNameTag.getTagsVisible().get(tags.getNameTag().getID())) {
        //         NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getAbove());
        //         NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getNameTag());
        //         NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getBelow());
        //         NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getAbove());
        //         NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getNameTag());
        //         NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getBelow());
        //         playerNameTag.getTagsVisible().put(tags.getNameTag().getID(), true);

        //         NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getAbove());
        //         NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getNameTag());
        //         NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getBelow());
        //         NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getAbove());
        //         NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getNameTag());
        //         NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getBelow());
        //         tags.getTagsVisible().put(playerNameTag.getNameTag().getID(), true);
        //     }
        // }

        // moveNameTag(event.getPlayer());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        new BukkitRunnable() { //Event is fired before the player is actually sneaking, so its bounding box
            @Override          //is still standing, thus we wait 2 ticks before updating the nametag
            public void run() {
                PlayerNameTag.get(event.getPlayer()).updateState();
            }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerNameTag nameTag = PlayerNameTag.get(event.getPlayer());
        if (nameTag != null) nameTag.destroy();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMountEntity(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        PlayerNameTag nameTag = PlayerNameTag.get((Player) event.getEntity());
        if (nameTag != null) nameTag.updatePosition();
    }
}
