package net.laboulangerie.laboulangeriecore.tab;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;


public class TabListener implements Listener {

    private final TabManager tabManager;

    private final LuckPerms lpApi;

    public TabListener() {
        this.tabManager = new TabManager();
        this.lpApi = LuckPermsProvider.get();

        final Set<Group> groups = lpApi.getGroupManager().getLoadedGroups();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (final Group g : groups) {
            final String teamName = g.getWeight()+g.getName();
            if (board.getTeam(teamName) != null) continue;
            board.registerNewTeam(teamName);
        }
    }

    private Team getTeam(Player player) {
        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return null;

        final String teamName = group.getWeight()+group.getName();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        return (board.getTeam(teamName));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Team team = getTeam(player);
        if (team == null) return;

        team.removeEntry(player.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        tabManager.loadTab(player);

        final Team team = getTeam(player);
        if (team == null) return;
        team.addEntry(player.getName());
    }
}
