package net.laboulangerie.laboulangeriecore.nametag;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.nms.NMSSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerNameTag playerNameTag = new PlayerNameTag(player);
        PlayerNameTag.nameTags.add(playerNameTag);

        LaBoulangerieCore.PLUGIN.getNameTagManager().updateNameTag(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag != null)
            PlayerNameTag.nameTags.remove(playerNameTag);
    }
}
