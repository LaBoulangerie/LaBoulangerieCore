package net.laboulangerie.laboulangeriecore.nametag;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class NameTagManager {

    private final LuckPerms lpApi;

    public Team getTeam(@Nonnull Player player) {
        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return null;

        final String teamName = group.getWeight()+group.getName();
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        return (board.getTeam(teamName));
    }

    public NameTagManager() {
        this.lpApi = LuckPermsProvider.get();
    }

    public void reload() {
        Bukkit.getOnlinePlayers().forEach(this::updateNameTag);
    }

    public void updateNameTag(@Nonnull Player player) {
        final Team team = getTeam(player);
        if (team == null) return;

        final User user = lpApi.getUserManager().getUser(player.getUniqueId());
        final Group group = lpApi.getGroupManager().getGroup(user.getPrimaryGroup());
        final String prefix = group.getCachedData().getMetaData().getPrefix();
        if (prefix != null) team.setPrefix(prefix);

    }
}
