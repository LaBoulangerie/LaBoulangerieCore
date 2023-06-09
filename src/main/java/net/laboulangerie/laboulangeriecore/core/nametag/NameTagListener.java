package net.laboulangerie.laboulangeriecore.core.nametag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        NameTagManager.idToPlayer.put(event.getPlayer().getEntityId(), event.getPlayer());
        new PlayerNameTag(event.getPlayer());
    }

    @EventHandler
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerNameTag nameTag = PlayerNameTag.get(player);
        if (nameTag == null) return;

        new BukkitRunnable() { // Event is fired before the effect is applied
            @Override // thus we wait 2 ticks before updating the name tag
            public void run() {
                nameTag.updateState();
            }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        new BukkitRunnable() { // Event is fired before the player is actually sneaking, so its bounding box
            @Override // is still standing, thus we wait 2 ticks before updating the name tag
            public void run() {
                PlayerNameTag.get(event.getPlayer()).updateState();
            }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerNameTag nameTag = PlayerNameTag.get(event.getPlayer());
        if (nameTag != null) {
            nameTag.destroy();
            PlayerNameTag.nameTags.remove(nameTag);
        }
        NameTagManager.idToPlayer.remove(event.getPlayer().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // We update the state because the player might go in or out
                                                    // spectator mode where he is invisible
    public void onChangeGamemode(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;

        PlayerNameTag nameTag = PlayerNameTag.get(event.getPlayer());
        if (nameTag == null) return;

        new BukkitRunnable() { // Event is fired before the player actually changed gamemode,
            @Override // thus we wait 2 ticks before updating the name tag
            public void run() {
                nameTag.updateState();
            }
        }.runTaskLater(LaBoulangerieCore.PLUGIN, 2);
    }
}
