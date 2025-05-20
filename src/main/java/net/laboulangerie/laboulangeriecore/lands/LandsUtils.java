package net.laboulangerie.laboulangeriecore.lands;

import org.bukkit.OfflinePlayer;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;

public class LandsUtils {
    public static Land getPlayerMainLandOrNull (LandsIntegration apiLands, OfflinePlayer target) {
        for(Land land : apiLands.getLands())
        {
            if(land.getDefaultArea().getTrustedPlayers().contains(target.getUniqueId())) 
            {
                return land;
            }
        }
        
        return null;
    }
    
}
