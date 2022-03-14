package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabManager {

    private TabRenderer tabRenderer;

    public TabManager() {
        this.tabRenderer = new TabRenderer();

        Bukkit.getOnlinePlayers().forEach(this::loadTab);
    }

    public void loadTab(Player player) {
        player.sendPlayerListHeaderAndFooter(
                tabRenderer.renderSection(player, TabSection.HEADER),
                tabRenderer.renderSection(player, TabSection.FOOTER));
        player.playerListName(tabRenderer.renderSection(player, TabSection.NAME));
    }
}
