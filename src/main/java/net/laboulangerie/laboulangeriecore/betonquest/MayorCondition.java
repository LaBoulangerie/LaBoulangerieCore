package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

public class MayorCondition implements PlayerCondition {
    @Override
    public boolean check(Profile profile) throws QuestRuntimeException {
        Resident resident = TownyUniverse.getInstance().getResident(profile.getPlayerUUID());
        return resident != null && resident.isMayor();
    }
}
