package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HousesStockCondition implements PlayerCondition {
    @Override
    public boolean check(Profile profile) throws QuestRuntimeException {
        return LaBoulangerieCore.nationHouseHolder.getFreeHouses().size() != 0;
    }
}
