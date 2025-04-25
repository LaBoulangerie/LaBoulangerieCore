package net.laboulangerie.laboulangeriecore.lands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.events.LandInvitePlayerEvent;
import me.angeschossen.lands.api.events.LandTrustPlayerEvent;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class LandsListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInviteInArea(LandInvitePlayerEvent event) {
        Player sender = Bukkit.getPlayer(event.getPlayerUUID());
        OfflinePlayer target = Bukkit.getOfflinePlayer(event.getTargetUUID());
        Area area = event.getArea();

        if(area.isDefault()){
            LandsIntegration apiLands = LandsIntegration.of(LaBoulangerieCore.PLUGIN);
            Land targetMainLand = LandsUtils.getPlayerMainLandOrNull(apiLands, target);

            if(targetMainLand != null) { // Annulation de l'évènement
                sender.sendMessage(
                    Component.text("[").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("Lands").color(NamedTextColor.DARK_GREEN))
                    .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(event.getLand().getColorName()).color(NamedTextColor.DARK_GREEN))
                    .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(target.getName()).color(NamedTextColor.DARK_AQUA))
                    .append(Component.text(" est déjà un habitant de ").color(NamedTextColor.GRAY))
                    .append(Component.text(targetMainLand.getColorName()).color(NamedTextColor.GREEN))
                    .append(Component.text(", vous ne pouvez pas l'inviter dans cette zone !").color(NamedTextColor.GRAY))
                );

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoinArea(LandTrustPlayerEvent event) {
        Land targetMainLand = LandsUtils.getPlayerMainLandOrNull(LandsIntegration.of(LaBoulangerieCore.PLUGIN), Bukkit.getOfflinePlayer(event.getTargetUUID()));
    
        // Annule l'évènement si l'area est une zone default et le joueur est déjà membre d'une zone default
        if(event.getArea().isDefault() && targetMainLand != null) event.getLand().untrustPlayer(event.getPlayerUUID());
    }
}
