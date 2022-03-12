package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.laboulangerie.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class TabRenderer {
    private ConfigurationSection configTabSection;

    public TabRenderer() {
        this.configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("tab");

    }

    public Component renderSection(Player player, TabSection tabSection) {
        String tabSectionFormat = configTabSection.getString(tabSection.name().toLowerCase());
        ComponentRenderer renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();

        Component parsedComponent = renderer.getPapiMiniMessage(player).deserialize(tabSectionFormat);

        return parsedComponent;
    }

}
