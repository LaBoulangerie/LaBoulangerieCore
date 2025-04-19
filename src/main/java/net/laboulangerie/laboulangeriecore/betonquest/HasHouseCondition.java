package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import me.angeschossen.lands.api.player.OfflinePlayer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HasHouseCondition implements PlayerCondition {
    public boolean check(Profile profile) throws QuestRuntimeException {
        /*OfflinePlayer resident = (OfflinePlayer) LaBoulangerieCore.apiLands.getOfflineLandPlayer(profile.getPlayerUUID());
        if (resident == null) return false;
        Nation nation = resident.getNationOrNull();
        if (nation == null) return false;
        return LaBoulangerieCore.nationHouseHolder.hasHouse(nation.getUUID());*/

        return false;
    }
}
