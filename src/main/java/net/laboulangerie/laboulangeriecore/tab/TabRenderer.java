package net.laboulangerie.laboulangeriecore.tab;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;

public class TabRenderer {
    public Component renderSection(Player player, TabSection tabSection) {
        String tabSectionFormat =
                LaBoulangerieCore.PLUGIN.getConfig().getString("tab." + tabSection.name().toLowerCase());
        ComponentRenderer renderer = LaBoulangerieCore.PLUGIN.getComponentRenderer();

        return renderer.getPapiMiniMessage(player).deserialize(tabSectionFormat);
    }
}
