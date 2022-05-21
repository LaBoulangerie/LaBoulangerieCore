package net.laboulangerie.laboulangeriecore.tab;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;

public class TabManager {

    private final TabRenderer tabRenderer;

    public TabManager() {
        this.tabRenderer = new TabRenderer();

        final LuckPerms lpApi = LuckPermsProvider.get();

        Bukkit.getOnlinePlayers().forEach(this::loadTab);
        updateTab();

        final Set<Group> groups = lpApi.getGroupManager().getLoadedGroups();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (final Group g : groups) {
            final String teamName = g.getWeight()+g.getName();
            if (board.getTeam(teamName) != null) continue;
            board.registerNewTeam(teamName);
        }
    }


    private void updateTab() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> loadTab(p));
            }
        }.runTaskTimerAsynchronously(LaBoulangerieCore.PLUGIN, 0L, 20L);
    }

    protected void loadTab(Player player) {
        player.sendPlayerListHeaderAndFooter(
                tabRenderer.renderSection(player, TabSection.HEADER),
                tabRenderer.renderSection(player, TabSection.FOOTER));
        player.playerListName(tabRenderer.renderSection(player, TabSection.NAME));
    }
}
