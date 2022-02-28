package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class TabRenderer {
    private ConfigurationSection configTabSection;

    public TabRenderer() {
        this.configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("tab");

    }

    public Component renderSection(Player player, TabSection tabSection) {
        String sectionFormat = configTabSection.getString(tabSection.name().toLowerCase());

        String papiParsedString = PlaceholderAPI.setPlaceholders(player, sectionFormat);
        Component parsedComponent = MiniMessage.get().parse(papiParsedString);

        return parsedComponent;
    }

}
