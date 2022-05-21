package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;

public class TabRenderer {
    private final ConfigurationSection configTabSection;

    public TabRenderer() {
        this.configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("tab");
    }

    public Component renderSection(Player player, TabSection tabSection) {
        String tabSectionFormat = configTabSection.getString(tabSection.name().toLowerCase());
        ComponentRenderer renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();

        return renderer.getPapiMiniMessage(player).deserialize(tabSectionFormat);
    }

}
