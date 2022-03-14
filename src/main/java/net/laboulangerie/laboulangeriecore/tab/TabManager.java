package net.laboulangerie.laboulangeriecore.tab;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class TabManager {

    private final TabRenderer tabRenderer;

    private final LuckPerms lpApi;

    public TabManager() {
        this.tabRenderer = new TabRenderer();
        this.lpApi = LuckPermsProvider.get();

        Bukkit.getOnlinePlayers().forEach(this::loadTab);

        final Set<Group> groups = lpApi.getGroupManager().getLoadedGroups();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (final Group g : groups) {
            final String teamName = g.getWeight()+g.getName();
            if (board.getTeam(teamName) != null) continue;
            board.registerNewTeam(teamName);
        }
    }

    protected Team getTeam(Player player) {
        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return null;

        final String teamName = group.getWeight()+group.getName();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        return (board.getTeam(teamName));
    }

    protected void loadTab(Player player) {
        player.sendPlayerListHeaderAndFooter(
                tabRenderer.renderSection(player, TabSection.HEADER),
                tabRenderer.renderSection(player, TabSection.FOOTER));
        player.playerListName(tabRenderer.renderSection(player, TabSection.NAME));
    }
}
