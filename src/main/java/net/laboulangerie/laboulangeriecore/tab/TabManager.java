package net.laboulangerie.laboulangeriecore.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class TabManager {
    private final LuckPerms lpApi;
    private final TabRenderer tabRenderer;
    private HashMap<Group, Short> groupsMap = new HashMap<>();

    public TabManager() {
        this.lpApi = LuckPermsProvider.get();
        this.tabRenderer = new TabRenderer();

        final LuckPerms lpApi = LuckPermsProvider.get();

        Bukkit.getOnlinePlayers().forEach(this::loadTab);
        updateTab();

        final Set<Group> groups = lpApi.getGroupManager().getLoadedGroups();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        List<Group> sortedGroups = new ArrayList<>();
        sortedGroups.addAll(groups);
        Collections.sort(sortedGroups, (a, b) -> (b.getWeight().isPresent() ? b.getWeight().getAsInt() : 0)
                - (a.getWeight().isPresent() ? a.getWeight().getAsInt() : 0));

        for (short i = 0; i < sortedGroups.size(); i++) {
            groupsMap.put(sortedGroups.get(i), i);
            final String teamName = String.format("%04d", i) + sortedGroups.get(i).getName();
            if (board.getTeam(teamName) != null)
                continue;

            Team team = board.registerNewTeam(teamName);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.setCanSeeFriendlyInvisibles(false);
        }
    }

    public Team getTeam(@Nonnull Player player) {
        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if (user == null)
            return null;

        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null)
            return null;

        final String teamName = (groupsMap.containsKey(group) ? groupsMap.get(group) : "" ) + group.getName();
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
        player.sendPlayerListHeaderAndFooter(tabRenderer.renderSection(player, TabSection.HEADER),
                tabRenderer.renderSection(player, TabSection.FOOTER));
        player.playerListName(tabRenderer.renderSection(player, TabSection.NAME));
    }
}
