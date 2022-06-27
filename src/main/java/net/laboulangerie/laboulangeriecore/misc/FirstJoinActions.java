package net.laboulangerie.laboulangeriecore.misc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class FirstJoinActions implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        OfflinePlayer player = event.getPlayer();
        if (player.hasPlayedBefore()) return;

        List<String> commands = LaBoulangerieCore.PLUGIN.getConfig().getStringList("first-join-commands");
        if (commands == null) return;

        commands.stream().forEach(cmd -> {
            Bukkit.getServer().getCommandMap().dispatch(Bukkit.getServer().getConsoleSender(), cmd.replaceAll("%player%", player.getName()));
        });
    }
}
