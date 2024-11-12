package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HasHouseCondition implements PlayerCondition {
    public boolean check(Profile profile) throws QuestRuntimeException {
        Resident resident = TownyUniverse.getInstance().getResidentOpt(profile.getPlayerUUID()).orElse(null);
        if (resident == null) return false;
        Nation nation = resident.getNationOrNull();
        if (nation == null) return false;
        return LaBoulangerieCore.nationHouseHolder.hasHouse(nation.getUUID());
    }
}
