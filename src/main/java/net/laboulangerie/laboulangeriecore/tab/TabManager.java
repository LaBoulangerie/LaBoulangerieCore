package net.laboulangerie.laboulangeriecore.tab;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.util.Set;

public class TabManager {

    private final LuckPerms lpApi;

    private final TabRenderer tabRenderer;

    public TabManager() {
        this.lpApi = LuckPermsProvider.get();
        this.tabRenderer = new TabRenderer();

        final LuckPerms lpApi = LuckPermsProvider.get();

        Bukkit.getOnlinePlayers().forEach(this::loadTab);
        updateTab();

        final Set<Group> groups = lpApi.getGroupManager().getLoadedGroups();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (final Group g : groups) {
            final String teamName = g.getName();
            if (board.getTeam(teamName) != null) continue;
            Team team = board.registerNewTeam(teamName);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
        }
    }

    public Team getTeam(@Nonnull Player player) {
        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return null;

        final String teamName = group.getName();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        return board.getTeam(teamName);
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
