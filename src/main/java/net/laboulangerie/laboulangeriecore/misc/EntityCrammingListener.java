package net.laboulangerie.laboulangeriecore.misc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class EntityCrammingListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Ne pas affecter les joueurs
        if (event.getEntity() instanceof Player) return;

        // Vérifier si la feature est activée
        if (!LaBoulangerieCore.PLUGIN.getConfig()
                .getBoolean("entity-cramming.disable-drops", true)) return;

        // Vérifier la cause de la mort
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent == null) return;

        if (damageEvent.getCause() == DamageCause.CRAMMING) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
}
