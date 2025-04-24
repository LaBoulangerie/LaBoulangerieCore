package net.laboulangerie.laboulangeriecore.lands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.events.LandInvitePlayerEvent;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class LandsListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInviteInArea(LandInvitePlayerEvent event) {
        LandsIntegration apiLands = LandsIntegration.of(LaBoulangerieCore.PLUGIN);
        Area area = event.getArea();
        Player sender = Bukkit.getPlayer(event.getPlayerUUID());
        OfflinePlayer target = Bukkit.getOfflinePlayer(event.getTargetUUID());

        if(area.isDefault()){
            for(Land land : apiLands.getLands()) {
                if(land.getDefaultArea().getTrustedPlayers().contains(target.getUniqueId())){
                    sender.sendMessage(
                        Component.text("[").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text("Lands").color(NamedTextColor.DARK_GREEN))
                        .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(event.getLand().getColorName()).color(NamedTextColor.DARK_GREEN))
                        .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.getName()).color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" est déjà un habitant de ").color(NamedTextColor.GRAY))
                        .append(Component.text(land.getColorName()).color(NamedTextColor.GREEN))
                        .append(Component.text(", vous ne pouvez pas l'inviter dans cette zone !").color(NamedTextColor.GRAY))
                    );

                    event.setCancelled(true);
                }
            }
        } else {
            area.trustPlayer(target.getUniqueId());

            sender.sendMessage(
                Component.text("[").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("Lands").color(NamedTextColor.DARK_GREEN))
                .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(event.getLand().getColorName()).color(NamedTextColor.DARK_GREEN))
                .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(target.getName()).color(NamedTextColor.DARK_AQUA))
                .append(Component.text(" a été ajouté à la zone " + area.getName() + ".").color(NamedTextColor.GRAY))
            );

            event.setCancelled(true);
        }
    }
}
