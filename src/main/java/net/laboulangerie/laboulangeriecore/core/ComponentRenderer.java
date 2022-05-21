package net.laboulangerie.laboulangeriecore.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class ComponentRenderer {

    private final FileConfiguration config;

    public ComponentRenderer() {
        this.config = LaBoulangerieCore.PLUGIN.getConfig();
    }

    public Component parse(OfflinePlayer player, String text) {

        Component miniMessageParsed = getPapiMiniMessage(player).deserialize(text, parseTags(player));

        return miniMessageParsed;
    }

    public Component parse(OfflinePlayer player, String text, TagResolver additionnalResolver) {
        TagResolver tagResolver = TagResolver.resolver(parseTags(player), additionnalResolver);

        Component miniMessageParsed = getPapiMiniMessage(player).deserialize(text, tagResolver);

        return miniMessageParsed;
    }

    private TagResolver parseTags(OfflinePlayer player) {
        List<TagResolver.Single> resolvers = new ArrayList<>();
        ConfigurationSection tagSection = config.getConfigurationSection("tags");

        for (String key : tagSection.getKeys(false)) {
            String tag = tagSection.getString(key);

            resolvers.add(
                    Placeholder.component(key,
                            getPapiMiniMessage(player).deserialize(tag)));
        }

        return TagResolver.resolver(resolvers);
    }

    public MiniMessage getPapiMiniMessage(OfflinePlayer player) {

        return MiniMessage.builder().tags(
                        TagResolver.builder()
                                .resolver(StandardTags.defaults())
                                .resolver(papiTagResolver(player))
                                .build())
                .build();
    }

    private TagResolver papiTagResolver(OfflinePlayer player) {

        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            String placeholder = argumentQueue
                    .popOr("The <papi> tag requires exactly one argument, the PAPI placeholder").value();

            String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');

            if (parsedPlaceholder.contains("§")) {
                return Tag
                        .selfClosingInserting(LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder));
            }

            return Tag.selfClosingInserting(MiniMessage.miniMessage().deserialize(parsedPlaceholder));
        });
    }
}
