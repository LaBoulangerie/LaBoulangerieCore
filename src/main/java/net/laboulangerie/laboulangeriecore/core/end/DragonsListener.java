package net.laboulangerie.laboulangeriecore.core.end;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
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
                        Thread.sleep(700);
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    implicatedPlayers.sendMessage(Component.text(i +". ")
                        .append(entry.getKey().displayName().color(TextColor.fromHexString("#555555")))
                        .append(Component.text("§0 - §f" + formatter.format(entry.getValue()) + "🗡")
                            .hoverEvent(HoverEvent.showText(Component.text("Dégats causés"))))
                        .append(Component.text(" [", TextColor.fromHexString("#4d4848")))
                        .append(Component.text(formatter.format(entry.getValue()/dragon.getTotalDamages()*100) + "%"))
                        .append(Component.text("]", TextColor.fromHexString("#4d4848")))
                    );
                    i++;
                    if (i > 10) break;
                }
                implicatedPlayers.sendMessage(Component.text("§5-----------------------------------------------"));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) { e.printStackTrace(); }

                for (Player player : dragon.getImplicatedPlayers()) {
                    double multiplier = dragon.getDamageDealt(player) / dragon.getTotalDamages();
                    double divineFavors = Math.round(LaBoulangerieCore.PLUGIN.getConfig().getInt("killing-dragon-reward") * multiplier);
                    player.sendMessage("Vous recevez §5" + divineFavors + "§f points divins.");
                    DivineFavorsHolder.giveDivineFavors(player, divineFavors);
                }
            }
        }.runTaskAsynchronously(LaBoulangerieCore.PLUGIN);

        dragon.destroy();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.DRAGON_EGG) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            EggManager.eggTeleported(event.getClickedBlock().getLocation());
            return;
        }

        EggManager.click(event.getPlayer(), event.getClickedBlock().getLocation());
        event.setCancelled(true);
    }
}
