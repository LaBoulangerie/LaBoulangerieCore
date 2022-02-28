package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TabListener implements Listener {

    private TabManager tabManager;

    public TabListener() {
        this.tabManager = new TabManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        tabManager.loadTab(player);
    }
}
