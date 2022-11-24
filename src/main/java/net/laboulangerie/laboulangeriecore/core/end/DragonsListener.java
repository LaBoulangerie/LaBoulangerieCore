package net.laboulangerie.laboulangeriecore.core.end;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import net.laboulangerie.laboulangeriecore.core.favors.DivineFavorsHolder;

public class DragonsListener implements Listener {
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
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
        formatter.applyPattern("#.##");

        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 1;
                implicatedPlayers.sendMessage(Component.text("§5-----------L'ender dragon a été vaincu !-----------"));
                for (Entry<Player, Double> entry : dragon.sortDamagers().entrySet()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    implicatedPlayers.sendMessage(Component.text(i +". ")
                        .append(entry.getKey().displayName().color(TextColor.fromHexString("#555555")))
                        .append(Component.text("§0 - §f" + formatter.format(entry.getValue()
                            + " §0[§f" + formatter.format(entry.getValue()/dragon.getTotalDamages()*100)
                            + "§0]"
                        )))
                    );
                    i++;
                    if (i > 5) break;
                }
                implicatedPlayers.sendMessage(Component.text("§5--------------------------------------------------"));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) { e.printStackTrace(); }

                for (Player player : dragon.getImplicatedPlayers()) {
                    double multiplier = Math.round(dragon.getDamageDealt(player) / dragon.getTotalDamages());
                    double divineFavors = LaBoulangerieCore.PLUGIN.getConfig().getInt("killing-dragon-reward") * multiplier;
                    player.sendMessage("Vous recevez §5" + divineFavors + "§f points divins.");
                    DivineFavorsHolder.giveDivineFavors(player, divineFavors);
                }
            }
        }.runTaskAsynchronously(LaBoulangerieCore.PLUGIN);

        dragon.destroy();
    }
}
