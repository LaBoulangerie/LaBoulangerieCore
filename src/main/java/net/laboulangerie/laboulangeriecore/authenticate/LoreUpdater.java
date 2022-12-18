package net.laboulangerie.laboulangeriecore.authenticate;

import com.palmergames.bukkit.towny.TownyUniverse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class LoreUpdater implements Listener {
    @EventHandler
    public void onPickupEvent(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Authenticable authenticable = new Authenticable(event.getItem().getItemStack());

        if (!authenticable.isAuthenticated()) return;

        String newName = null;

        switch (authenticable.getAuthorityType()) {
            case PLAYER:
                newName = Bukkit.getOfflinePlayer(authenticable.getAuthorityUUID()).getName();
                break;
            case TOWN:
                // If the town no longer exists we leave the lore as is
                if (!TownyUniverse.getInstance().hasTown(authenticable.getAuthorityUUID())) return;
                newName = TownyUniverse.getInstance().getTown(authenticable.getAuthorityUUID()).getName();
                break;
            case NATION:
                // If the nation no longer exists we leave the lore as is
                if (!TownyUniverse.getInstance().hasNation(authenticable.getAuthorityUUID())) return;
                newName = TownyUniverse.getInstance().getNation(authenticable.getAuthorityUUID()).getName();
                break;
            default:
                break;
        }

        if (newName == null || newName.equals(authenticable.getAuthorityName())) return;

        authenticable.updateAuthorityName(newName);
    }
}
