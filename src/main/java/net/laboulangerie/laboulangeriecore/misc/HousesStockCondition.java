package net.laboulangerie.laboulangeriecore.misc;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class HousesStockCondition extends Condition {
    @SuppressWarnings("deprecation")
    public HousesStockCondition(Instruction instruction) {
        super(instruction);
    }
    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        return LaBoulangerieCore.nationHouseHolder.getFreeHouses().size() != 0;
    }
}
