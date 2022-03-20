package net.laboulangerie.core;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentRenderer {

    public ComponentRenderer() {
    }

    public MiniMessage getPapiMiniMessage(Player player) {

        return MiniMessage.builder().tags(
                TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTagResolver(player))
                        .build())
                .build();
    }

    private TagResolver papiTagResolver(Player player) {

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
