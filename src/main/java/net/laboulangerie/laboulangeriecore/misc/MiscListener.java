package net.laboulangerie.laboulangeriecore.misc;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class MiscListener implements Listener {
    ConfigurationSection miscSection;
    HashMap<UUID, Date> crystalDelay = new HashMap<>();

    public MiscListener() {
        this.miscSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("misc");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));

        if (data.getString("nick") != null) {
            player.displayName(Component.text(data.getString("nick")));
        }

        if (player.hasPlayedBefore()) return;

        List<String> commands = LaBoulangerieCore.PLUGIN.getConfig().getStringList("first-join-commands");
        if (commands == null) return;

        commands.stream().forEach(cmd -> {
            Bukkit.getServer().getCommandMap().dispatch(Bukkit.getServer().getConsoleSender(), cmd.replaceAll("%player%", player.getName()));
        });
    }

    // ResourcePack
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

        if (!LaBoulangerieCore.PLUGIN.getConfig().isSet("resource-pack-sha1") || !LaBoulangerieCore.PLUGIN.getConfig().isSet("resource-pack-url")) return;

        player.setResourcePack(LaBoulangerieCore.PLUGIN.getConfig().getString("resource-pack-url"), LaBoulangerieCore.PLUGIN.getConfig().getString("resource-pack-sha1"), true);
        player.setInvulnerable(true);
    }

    @EventHandler
    public void onCrystalExplode(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.ENDER_CRYSTAL) return;

        event.setDamage(event.getDamage() * (1 - LaBoulangerieCore.PLUGIN.getConfig().getDouble("crystal-nerf-percentage")/100));
    }

    @EventHandler
    public void onBlockExplode(EntityDamageByBlockEvent event) {
        if (!event.getCause().equals(DamageCause.BLOCK_EXPLOSION)) return;

        event.setDamage(event.getDamage() * (1 - LaBoulangerieCore.PLUGIN.getConfig().getDouble("crystal-nerf-percentage")/100));
    }

    @EventHandler
    public void onPlaceCrystal(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.useItemInHand() == Result.DENY || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.OBSIDIAN || event.getItem() == null || event.getItem().getType() != Material.END_CRYSTAL) return;

        if (!crystalDelay.containsKey(event.getPlayer().getUniqueId())) {
            crystalDelay.put(event.getPlayer().getUniqueId(), new Date());
            return;
        }
        Date latestCrystal = crystalDelay.get(event.getPlayer().getUniqueId());
        if (new Date().getTime() - latestCrystal.getTime() <= 2000) {
            event.getPlayer().sendActionBar(Component.text("§cVous devez attendre " + (2000 - (new Date().getTime() - latestCrystal.getTime()))/1000 + " secondes"));
            event.setCancelled(true);
        } else {
            crystalDelay.replace(event.getPlayer().getUniqueId(), new Date());
        }
    }
}
