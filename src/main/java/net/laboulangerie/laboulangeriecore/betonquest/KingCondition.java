package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.lands.LandsUtils;

public class KingCondition implements PlayerCondition {
    public boolean check(Profile profile) throws QuestRuntimeException {
        Land mainLand = LandsUtils.getPlayerMainLandOrNull(LaBoulangerieCore.apiLands, profile.getPlayer());

        if(mainLand != null){
            if(mainLand.getNation().getCapital().equals(mainLand)){
                Area area = mainLand.getDefaultArea();
                return area.hasRoleFlag(profile.getPlayerUUID(), LaBoulangerieCore.nationAuthenticateFlag);
            }
        }

        return false;
    }
}
