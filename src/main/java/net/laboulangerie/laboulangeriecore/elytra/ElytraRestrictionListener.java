package net.laboulangerie.laboulangeriecore.elytra;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.elytra.ElytraManager.RestrictionReason;

public class ElytraRestrictionListener implements Listener {

    private final ElytraManager elytraManager;

    public ElytraRestrictionListener(ElytraManager elytraManager) {
        this.elytraManager = elytraManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!elytraManager.isEnabled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.isGliding()) return;

        RestrictionReason reason = elytraManager.getRestrictionReason(player);

        switch (reason) {
            case RAIN -> {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize(elytraManager.getRainMessage()));
            }
            case COMMAND -> {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize(elytraManager.getCommandMessage()));
            }
            case WIND -> {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize(elytraManager.getWindMessage()));
            }
            case NONE -> {}
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!elytraManager.isEnabled()) return;
        if (!elytraManager.isRainRestrictionEnabled()) return;
        if (event.isCancelled()) return;

        if (event.toWeatherState()) {
            elytraManager.forceStopGlidingPlayersInWorld(event.getWorld(), elytraManager.getRainStartMessage());
        }
    }
}
