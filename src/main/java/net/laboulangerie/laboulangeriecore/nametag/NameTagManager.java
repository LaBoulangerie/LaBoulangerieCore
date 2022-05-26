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
import org.bukkit.scoreboard.*;

import javax.annotation.Nonnull;

public class NameTagManager {


    private final ConfigurationSection configTabSection;
    private final ComponentRenderer renderer;

    public NameTagManager() {

        this.configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("nametag");
        this.renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();
    }

    public void reload() {
        Bukkit.getOnlinePlayers().forEach(this::updateNameTag);
    }

    private void setAbove(@Nonnull Player player) {
        final String section = configTabSection.getString("above");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag == null) return;

        Bukkit.getOnlinePlayers().forEach(p -> playerNameTag.spawnNameTag(p, playerNameTag.getAbove(), component));
    }

    private void setNameTag(@Nonnull Player player) {
        final String section = configTabSection.getString("nametag");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag == null) return;

        Bukkit.getOnlinePlayers().forEach(p -> playerNameTag.spawnNameTag(p, playerNameTag.getNameTag(), component));
    }

    private void setBelow(@Nonnull Player player) {
        final String section = configTabSection.getString("below");
        final Component component = renderer.getPapiMiniMessage(player).deserialize(section);
        final PlayerNameTag playerNameTag = PlayerNameTag.get(player);
        if (playerNameTag == null) return;

        Bukkit.getOnlinePlayers().forEach(p -> playerNameTag.spawnNameTag(p, playerNameTag.getBelow(), component));
    }

    public void updateNameTag(@Nonnull Player player) {
        setAbove(player);
        setNameTag(player);
        setBelow(player);
    }
}
