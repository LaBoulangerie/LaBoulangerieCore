package net.laboulangerie.laboulangeriecore.nametag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class NameTagManager {

    private final LuckPerms lpApi;
    private final ConfigurationSection configTabSection;
    private final ComponentRenderer renderer;

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
        this.configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("nametag");
        this.renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();
    }

    public void reload() {
        Bukkit.getOnlinePlayers().forEach(this::updateNameTag);
    }

    private void setPrefix(@Nonnull Player player, @Nonnull Team team) {
        final String section = configTabSection.getString("prefix");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        team.prefix(component);
    }

    private void setColor(@Nonnull Player player, @Nonnull Team team) {
        final String section = configTabSection.getString("color");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        team.color((NamedTextColor) component.color());
    }

    private void setSuffix(@Nonnull Player player, @Nonnull Team team) {
        final String section = configTabSection.getString("suffix");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        team.suffix(component);
    }

    public void updateNameTag(@Nonnull Player player) {
        final Team team = getTeam(player);
        if (team == null) return;

        setPrefix(player, team);
        setColor(player, team);
        setSuffix(player, team);


    }
}
