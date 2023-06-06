package net.laboulangerie.laboulangeriecore.speedpaths;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class SpeedPathListener implements Listener {
    private final static float PLAYER_DEFAULT_SPEED = 0.2f;
    private final static int SPEED_TICK_COOLDOWN = 20;
    private SpeedPathManager speedPathManager;
    private Map<UUID, String> playerPaths = new HashMap<>();

    @EventHandler
    void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        this.speedPathManager = LaBoulangerieCore.PLUGIN.getSpeedPathManager();
        Map<String, SpeedPath> paths = speedPathManager.getPaths();

        if (paths.isEmpty())
            return;

        for (String pathKey : paths.keySet()) {
            SpeedPath path = paths.get(pathKey);

            if (!playerPaths.containsKey(player.getUniqueId()) && path.isOnIt(player.getLocation())) {
                player.setWalkSpeed(path.getSpeed());
                playerPaths.put(player.getUniqueId(), pathKey);
                break;
            }
        }

        if (hasLeftPath(player)) {
            offRunnable(player).runTaskLater(LaBoulangerieCore.PLUGIN, SPEED_TICK_COOLDOWN);
        }
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
                    player.setWalkSpeed(PLAYER_DEFAULT_SPEED);
                    playerPaths.remove(player.getUniqueId());
                }
            }

        };
    }
}
