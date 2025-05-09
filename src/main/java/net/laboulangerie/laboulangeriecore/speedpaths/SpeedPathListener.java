package net.laboulangerie.laboulangeriecore.speedpaths;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class SpeedPathListener implements Listener {
    private final static float PLAYER_DEFAULT_SPEED = 0.2f;
    private final static int SPEED_TICK_COOLDOWN = 20;
    private SpeedPathManager speedPathManager;
    private Map<UUID, String> playerPaths = new HashMap<>();

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        this.speedPathManager = LaBoulangerieCore.PLUGIN.getSpeedPathManager();
        Map<String, SpeedPath> paths = speedPathManager.getPaths();

        if (paths.isEmpty())
            return;

        if (hasLeftPath(player)) {
            offRunnable(player).runTaskLater(LaBoulangerieCore.PLUGIN, SPEED_TICK_COOLDOWN);
            return;
        }

        YamlConfiguration playerData = UsersData.getOrCreate(player);
        if (!playerData.getBoolean("enable-speed-path", true))
            return;

        for (String pathKey : paths.keySet()) {
            SpeedPath path = paths.get(pathKey);

            if (!playerPaths.containsKey(player.getUniqueId()) && path.isOnIt(player.getLocation())) {
                goFast(player, path.getSpeed());
                playerPaths.put(player.getUniqueId(), pathKey);
                break;
            }
        }
    }

    @EventHandler
    void onPlayerDismount(EntityDismountEvent event) {
        if (!(event.getDismounted() instanceof LivingEntity) || !(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        if (!playerPaths.containsKey(player.getUniqueId()))
            return;

        LivingEntity mount = (LivingEntity) event.getDismounted();
        mount.removePotionEffect(PotionEffectType.SPEED);
    }

    @EventHandler
    void onPlayerMount(EntityMountEvent event) {
        if (!(event.getMount() instanceof LivingEntity) || !(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        if (!playerPaths.containsKey(player.getUniqueId()))
            return;

        LivingEntity mount = (LivingEntity) event.getMount();
        mount.addPotionEffect(getPotionEffectForSpeed(player.getWalkSpeed()));
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        goNormal(event.getPlayer());
    }

    private boolean hasLeftPath(Player player) {
        return playerPaths.containsKey(player.getUniqueId())
                && !speedPathManager.getPath(playerPaths.get(player.getUniqueId())).isOnIt(player.getLocation());
    }

    private BukkitRunnable offRunnable(Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (hasLeftPath(player)) {
                    goNormal(player);
                    playerPaths.remove(player.getUniqueId());
                }
            }
        };
    }

    private void goFast(Player player, float speed) {
        player.setWalkSpeed(speed);
        if (player.getVehicle() != null && player.getVehicle() instanceof LivingEntity mount) {
            mount.addPotionEffect(getPotionEffectForSpeed(speed));
        }
    }

    private void goNormal(Player player) {
        player.setWalkSpeed(PLAYER_DEFAULT_SPEED);
        if (player.getVehicle() != null && player.getVehicle() instanceof LivingEntity mount) {
            mount.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    private PotionEffect getPotionEffectForSpeed(float speed) {
        return new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, (int) (10 * speed), false,
                false);
    }
}
