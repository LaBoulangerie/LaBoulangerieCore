package net.laboulangerie.laboulangeriecore.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class MiscListener implements Listener {

    private List<UUID> invulnerablePlayers = new ArrayList<>();
    private PotionEffect blindnessEffect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));

        if (data.getString("nick") != null) {
            player.displayName(Component.text(data.getString("nick")));
        }

        if (player.hasPlayedBefore())
            return;

        List<String> commands = LaBoulangerieCore.PLUGIN.getConfig().getStringList("first-join-commands");
        if (commands == null)
            return;

        commands.stream().forEach(cmd -> {
            Bukkit.getServer().getCommandMap().dispatch(Bukkit.getServer().getConsoleSender(),
                    cmd.replaceAll("%player%", player.getName()));
        });
    }

    // ResourcePack
    @EventHandler
    private void onPlayerResourcePackLoad(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (event.getStatus() == Status.DECLINED) {
            Component kickMessage = MiniMessage.miniMessage()
                    .deserialize(LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("misc")
                            .getString("declined-pack-kick-msg"));
            player.kick(kickMessage);
        }
    }

    @EventHandler
    public void onJoinResourcePack(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
        data.set("last-ip-address", player.getAddress().getHostString());
        try {
            UsersData.save(player, data);
        } catch (IOException e) {
            LaBoulangerieCore.PLUGIN.getLogger().severe("Unable to save " + player.getName() + " data:");
            e.printStackTrace();
        }

        if (!LaBoulangerieCore.PLUGIN.getConfig().isSet("resource-pack-sha1")
                || !LaBoulangerieCore.PLUGIN.getConfig().isSet("resource-pack-url"))
            return;

        player.setResourcePack(LaBoulangerieCore.PLUGIN.getConfig().getString("resource-pack-url"),
                LaBoulangerieCore.PLUGIN.getConfig().getString("resource-pack-sha1"), true);
    }

    // Invulnerability at connexion and portal travel
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        vegetableizePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        vegetableizePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!invulnerablePlayers.contains(player.getUniqueId()))
            return;
        player.setInvulnerable(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        invulnerablePlayers.remove(player.getUniqueId());
    }

    private void vegetableizePlayer(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        player.setInvulnerable(true);
        player.addPotionEffect(blindnessEffect);
        invulnerablePlayers.add(player.getUniqueId());
    }
}
