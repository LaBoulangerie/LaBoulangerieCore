package net.laboulangerie.laboulangeriecore.nametag;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p == player) return;
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

        toggleTextVisibility(!player.isInvisible(), above);
        toggleTextVisibility(!player.isInvisible(), nameTagAbove);
        toggleTextVisibility(!player.isInvisible(), below);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (player == p) return;
            NMSEntityMetadata.send(p, above);
            NMSEntityMetadata.send(p, nameTagAbove);
            NMSEntityMetadata.send(p, below);
            NMSEntityTeleport.send(p, above, location.getX(), location.getY() + 2.4 - sneakHeight, location.getZ());
            NMSEntityTeleport.send(p, nameTagAbove, location.getX(), location.getY() + 2.1 - sneakHeight, location.getZ());
            NMSEntityTeleport.send(p, below, location.getX(), location.getY() + 1.8 - sneakHeight, location.getZ());
        });
    }

    private void toggleTextVisibility(boolean isVisible, NMSEntities entity) {
        try {
            final Method setCustomNameVisible = entity.getEntity().getClass().getMethod("n", boolean.class);
            setCustomNameVisible.invoke(entity.getEntity(), isVisible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag == null) return;

        if (event.getAction().equals(EntityPotionEffectEvent.Action.ADDED) && event.getNewEffect() != null &&
                event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            toggleTextVisibility(false, playerNameTag.getAbove());
            toggleTextVisibility(false, playerNameTag.getNameTag());
            toggleTextVisibility(false, playerNameTag.getBelow());
        } else if (event.getAction().equals(EntityPotionEffectEvent.Action.REMOVED) && event.getOldEffect() != null &&
                event.getOldEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            toggleTextVisibility(true, playerNameTag.getAbove());
            toggleTextVisibility(true, playerNameTag.getNameTag());
            toggleTextVisibility(true, playerNameTag.getBelow());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player == p) return;
            NMSEntityMetadata.send(p, playerNameTag.getAbove());
            NMSEntityMetadata.send(p, playerNameTag.getNameTag());
            NMSEntityMetadata.send(p, playerNameTag.getBelow());
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        final PlayerNameTag playerNameTag = PlayerNameTag.get(event.getPlayer());
        if (playerNameTag == null) return;

        final Location location = event.getTo();

        final NMSEntities above = playerNameTag.getAbove();
        final NMSEntities nameTagAbove = playerNameTag.getNameTag();
        final NMSEntities below = playerNameTag.getBelow();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p == event.getPlayer()) return;
            NMSEntityTeleport.send(p, above, location.getX(), location.getY() + 2.4, location.getZ());
            NMSEntityTeleport.send(p, nameTagAbove, location.getX(), location.getY() + 2.1, location.getZ());
            NMSEntityTeleport.send(p, below, location.getX(), location.getY() + 1.8, location.getZ());
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final PlayerNameTag playerNameTag = PlayerNameTag.get(event.getPlayer());
        if (playerNameTag == null) return;

        for (PlayerNameTag tags : PlayerNameTag.nameTags) {
            if (tags == playerNameTag) continue;
            playerNameTag.getTagsVisible().putIfAbsent(tags.getNameTag().getID(), false);

            double distance = Math.pow(event.getPlayer().getLocation().getX() - tags.getPlayer().getLocation().getX(), 2) +
                            Math.pow(event.getPlayer().getLocation().getY() - tags.getPlayer().getLocation().getY(), 2) +
                            Math.pow(event.getPlayer().getLocation().getZ() - tags.getPlayer().getLocation().getZ(), 2);

            if (distance > 2000 && playerNameTag.getTagsVisible().get(tags.getNameTag().getID())) {
                NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getAbove().getID());
                NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getNameTag().getID());
                NMSEntityDestroy.send(playerNameTag.getPlayer(), tags.getBelow().getID());
                playerNameTag.getTagsVisible().put(tags.getNameTag().getID(), false);

                NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getAbove().getID());
                NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getNameTag().getID());
                NMSEntityDestroy.send(tags.getPlayer(), playerNameTag.getBelow().getID());
                tags.getTagsVisible().put(playerNameTag.getNameTag().getID(), false);
            }

            if (distance < 2000 && !playerNameTag.getTagsVisible().get(tags.getNameTag().getID())) {
                NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getAbove());
                NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getNameTag());
                NMSSpawnEntityLiving.send(playerNameTag.getPlayer(), tags.getBelow());
                NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getAbove());
                NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getNameTag());
                NMSEntityMetadata.send(playerNameTag.getPlayer(), tags.getBelow());
                playerNameTag.getTagsVisible().put(tags.getNameTag().getID(), true);

                NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getAbove());
                NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getNameTag());
                NMSSpawnEntityLiving.send(tags.getPlayer(), playerNameTag.getBelow());
                NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getAbove());
                NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getNameTag());
                NMSEntityMetadata.send(tags.getPlayer(), playerNameTag.getBelow());
                tags.getTagsVisible().put(playerNameTag.getNameTag().getID(), true);
            }
        }

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
                if (player == p) return;
                NMSEntityDestroy.send(p, playerNameTag.getAbove().getID());
                NMSEntityDestroy.send(p, playerNameTag.getNameTag().getID());
                NMSEntityDestroy.send(p, playerNameTag.getBelow().getID());
            });
        }
    }
}
