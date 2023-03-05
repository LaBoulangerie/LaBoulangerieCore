package net.laboulangerie.laboulangeriecore.misc;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class ResourcePackListener implements Listener {
    ConfigurationSection miscSection;

    public ResourcePackListener() {
        this.miscSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("misc");
    }

    @EventHandler
    private void onPlayerResourcePackLoad(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
            case ACCEPTED:
                player.setInvulnerable(true);
                break;

            case DECLINED:
                Component kickMessage = MiniMessage.miniMessage()
                        .deserialize(miscSection.getString("declined-pack-kick-msg"));
                player.kick(kickMessage);
                break;

            case FAILED_DOWNLOAD:
            case SUCCESSFULLY_LOADED:
                player.setInvulnerable(false);

            default:
                break;
        }
    }
}
