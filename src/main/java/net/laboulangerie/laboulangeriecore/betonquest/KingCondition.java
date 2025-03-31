package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class KingCondition implements PlayerCondition {
    public boolean check(Profile profile) throws QuestRuntimeException {
        LandPlayer resident = LaBoulangerieCore.apiLands.getLandPlayer(profile.getPlayerUUID());

        for( Land land : resident.getLands()){
            if(land.getNation().getOwnerUID().equals(profile.getPlayerUUID())){
                return true;
            }
        }

        return false;
    }
}
