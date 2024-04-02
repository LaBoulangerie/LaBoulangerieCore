package net.laboulangerie.laboulangeriecore.misc;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class MiscListener implements Listener {
    ConfigurationSection miscSection;
    HashMap<UUID, Date> crystalDelay = new HashMap<>();

    private Map<UUID, Location> invulnerablePlayers = new HashMap<>();
    private PotionEffect blindnessEffect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true,
            false, false);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        vegetableizePlayer(player); // Invulnerability at connection and portal travel

        YamlConfiguration data = UsersData.getOrCreate(player);

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
            Component kickMessage = MiniMessage.miniMessage().deserialize(LaBoulangerieCore.PLUGIN.getConfig()
                    .getConfigurationSection("misc").getString("declined-pack-kick-msg"));
            player.kick(kickMessage);
        }
    }

    @EventHandler
    public void onJoinResourcePack(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        YamlConfiguration data = UsersData.getOrCreate(player);
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

    // Invulnerability at connection and portal travel
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        vegetableizePlayer(event.getPlayer());
    }

    private void vegetableizePlayer(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        player.setInvulnerable(true);
        player.setCollidable(false);
        player.addPotionEffect(blindnessEffect);
        invulnerablePlayers.put(player.getUniqueId(), player.getEyeLocation());
    }

    public void registerProtocolLibListeners() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(LaBoulangerieCore.PLUGIN, PacketType.Play.Client.POSITION) {
                    public void onPacketReceiving(PacketEvent event) {
                        performAfkCheck(event.getPlayer());
                    }
                });
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(LaBoulangerieCore.PLUGIN, PacketType.Play.Client.POSITION_LOOK) {
                    public void onPacketReceiving(PacketEvent event) {
                        performAfkCheck(event.getPlayer());
                    }
                });
    }

    private boolean orientationEquals(Vector vec1, Vector vec2) {
        return vec1.getX() == vec2.getX() && vec1.getY() == vec2.getY() && vec1.getZ() == vec2.getZ();
    }

    private void performAfkCheck(Player player) {
        if (invulnerablePlayers.containsKey(player.getUniqueId())) {
            if (invulnerablePlayers.get(player.getUniqueId()).getWorld() != player.getWorld()) {
                invulnerablePlayers.replace(player.getUniqueId(), player.getEyeLocation());
                return;
            }
            if (!orientationEquals(invulnerablePlayers.get(player.getUniqueId()).getDirection(),
                    player.getEyeLocation().getDirection())
                    || !isCardinalMove(invulnerablePlayers.get(player.getUniqueId()), player.getEyeLocation())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setInvulnerable(false);
                        player.setCollidable(true);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        invulnerablePlayers.remove(player.getUniqueId());
                    }
                }.runTask(LaBoulangerieCore.PLUGIN);
            }
        }
    }

    private boolean isCardinalMove(Location loc1, Location loc2) {
        Vector vec = loc1.toVector().subtract(loc2.toVector());
        if (vec.equals(new Vector(0, 0, 0)))
            return true;
        return (vec.getX() != 0 && vec.getY() == 0 && vec.getZ() == 0)
                || (vec.getX() == 0 && vec.getY() != 0 && vec.getZ() == 0)
                || (vec.getX() == 0 && vec.getY() == 0 && vec.getZ() != 0);
    }

    @EventHandler
    public void onCrystalExplode(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.ENDER_CRYSTAL)
            return;

        event.setDamage(event.getDamage()
                * (1 - LaBoulangerieCore.PLUGIN.getConfig().getDouble("crystal-nerf-percentage") / 100));
    }

    @EventHandler
    public void onBlockExplode(EntityDamageByBlockEvent event) {
        if (!event.getCause().equals(DamageCause.BLOCK_EXPLOSION))
            return;

        event.setDamage(event.getDamage()
                * (1 - LaBoulangerieCore.PLUGIN.getConfig().getDouble("crystal-nerf-percentage") / 100));
    }

    @EventHandler
    public void onPlaceCrystal(EntityPlaceEvent event) {
        if (event.getEntityType() != EntityType.ENDER_CRYSTAL)
            return;

        if (!crystalDelay.containsKey(event.getPlayer().getUniqueId())) {
            crystalDelay.put(event.getPlayer().getUniqueId(), new Date());
            return;
        }

        Date latestCrystal = crystalDelay.get(event.getPlayer().getUniqueId());
        if (new Date().getTime() - latestCrystal.getTime() <= LaBoulangerieCore.PLUGIN.getConfig()
                .getDouble("crystal-cooldown")) {
            DecimalFormat formatter = new DecimalFormat("0.00");
            event.getPlayer()
                    .sendActionBar(
                            Component.text("§cVous devez attendre "
                                    + formatter
                                            .format((LaBoulangerieCore.PLUGIN.getConfig().getDouble("crystal-cooldown")
                                                    - (new Date().getTime() - latestCrystal.getTime())) / 1000)
                                    + " secondes"));
            event.setCancelled(true);
        } else {
            crystalDelay.replace(event.getPlayer().getUniqueId(), new Date());
        }
    }

    // Obfuscate killer name in player death message
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // PvP kill only
        if (!(event.getEntity().getKiller() instanceof Player))
            return;

        String killerName = event.getEntity().getKiller().getName();
        Component obfuscatedKiller = Component.text("??????").decoration(TextDecoration.OBFUSCATED, true);
        TextReplacementConfig killerReplacement = TextReplacementConfig.builder().matchLiteral(killerName)
                .replacement(obfuscatedKiller).build();

        Component newDeathMessage = event.deathMessage().replaceText(killerReplacement);
        event.deathMessage(newDeathMessage);
    }
}
