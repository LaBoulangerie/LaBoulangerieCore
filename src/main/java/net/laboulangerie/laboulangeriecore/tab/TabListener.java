package net.laboulangerie.laboulangeriecore.tab;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

public class TabListener implements Listener {

    private final TabManager tabManager;

    public TabListener() {
        this.tabManager = new TabManager();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Team team = tabManager.getTeam(player);
        if (team == null) return;

        team.removeEntry(player.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        tabManager.loadTab(player);

        final Team team = tabManager.getTeam(player);
        if (team == null) return;

        team.addEntry(player.getName());
    }
}
