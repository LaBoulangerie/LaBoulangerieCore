package net.laboulangerie.laboulangeriecore.core.end;

import java.util.Map.Entry;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class DragonsListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER
            || event.getEntityType() != EntityType.ENDER_DRAGON
            || !Dragon.DRAGONS.containsKey(event.getEntity().getUniqueId())) return;

        Dragon dragon = Dragon.DRAGONS.get(event.getEntity().getUniqueId());
        dragon.dealDamage((Player) event.getDamager(), event.getDamage());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ENDER_DRAGON
            || !Dragon.DRAGONS.containsKey(event.getEntity().getUniqueId())) return;

        Dragon dragon = Dragon.DRAGONS.get(event.getEntity().getUniqueId());
        Audience implicatedPlayers = Audience.audience(dragon.getImplicatedPlayers());

        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 1;
                implicatedPlayers.sendMessage(Component.text("§5-----------L'ender dragon a été vaincu !-----------"));
                for (Entry<Player, Double> entry : dragon.sortDamagers().entrySet()) {
                    implicatedPlayers.sendMessage(Component.text(i +". ").append(entry.getKey().displayName().color(TextColor.fromHexString("#AAAAAA"))).append(Component.text(" - §f" + entry.getValue())));
                    i++;
                }
            }
        }.runTaskAsynchronously(LaBoulangerieCore.PLUGIN);

        dragon.destroy();
    }
}
