package net.laboulangerie.laboulangeriecore.misc;

import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class ResourcePackListener implements Listener {
    ConfigurationSection miscSection;

    public ResourcePackListener() {
        this.miscSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("misc");
    }

    @EventHandler
    private void onPlayerResourcePackLoad(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
        data.set("last-ip-address", player.getAddress().getHostString());
        try {
            UsersData.save(player, data);
        } catch (IOException e) {
            LaBoulangerieCore.PLUGIN.getLogger().severe("Unable to save " + player.getName() + " data:");
            e.printStackTrace();
        }

        if (!LaBoulangerieCore.PLUGIN.getConfig().isSet("resource-pack-sha1")) return;

        player.setResourcePack("https://laboulangerie.net/share/BreadDough.zip", LaBoulangerieCore.PLUGIN.getConfig().getString("resource-pack-sha1"), true);
        player.setInvulnerable(true);
    }
}
