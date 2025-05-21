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
    @EventHandler(priority = EventPriority.LOWEST) // Annulation de l'évènement lorsqu'un joueur, déjà membre d'un land, est invité dans un land
    private void onInviteInArea(LandInvitePlayerEvent event) {
        Player sender = Bukkit.getPlayer(event.getPlayerUUID());
        OfflinePlayer target = Bukkit.getOfflinePlayer(event.getTargetUUID());
        Area area = event.getArea();
        LandsIntegration apiLands = LandsIntegration.of(LaBoulangerieCore.PLUGIN);
        Land targetMainLand = LandsUtils.getPlayerMainLandOrNull(apiLands, target);

        if(targetMainLand != null){ // Annulation de l'évènement
            if(area == null) {
                sender.sendMessage(
                    Component.text("[").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("Lands").color(NamedTextColor.DARK_GREEN))
                    .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(event.getLand().getColorName()).color(NamedTextColor.DARK_GREEN))
                    .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(target.getName()).color(NamedTextColor.DARK_AQUA))
                    .append(Component.text(" est déjà un habitant de ").color(NamedTextColor.GRAY))
                    .append(Component.text(targetMainLand.getColorName()).color(NamedTextColor.GREEN))
                    .append(Component.text(", cette fonctionnalité ne peut pas être utilisée sur ce joueur !").color(NamedTextColor.GRAY))
                );
                event.setCancelled(true);
            } else if(area.isDefault()) {
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

    @EventHandler(priority = EventPriority.LOWEST) // Annulation de l'évènement lorsqu'un joueur, déjà membre d'un land, rejoint un land ouvert
    private void onJoinArea(LandTrustPlayerEvent event) {
        Player target;
        try {
            target = Bukkit.getPlayer(event.getTargetUUID());
        } catch (Exception e) {
            target = null;
        }

        if(target == null) return;

        Area area = event.getArea();
        LandsIntegration apiLands = LandsIntegration.of(LaBoulangerieCore.PLUGIN);
        Land targetMainLand = LandsUtils.getPlayerMainLandOrNull(apiLands, target);

        if(targetMainLand != null) {
            if(area != null) {
                if(area.isDefault()) {
                    target.sendMessage(
                        Component.text("[").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text("Lands").color(NamedTextColor.DARK_GREEN))
                        .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(event.getLand().getColorName()).color(NamedTextColor.DARK_GREEN))
                        .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(" vous êtes déjà un habitant de ").color(NamedTextColor.GRAY))
                        .append(Component.text(targetMainLand.getColorName()).color(NamedTextColor.GREEN))
                        .append(Component.text(", vous ne pouvez pas rejoindre une autre ville !").color(NamedTextColor.GRAY))
                    );
                    event.setCancelled(true);
                }
            }
        }
    }
}
