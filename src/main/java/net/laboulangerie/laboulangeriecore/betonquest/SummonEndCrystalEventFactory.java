package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

public class SummonEndCrystalEventFactory implements PlayerActionFactory {

    @Override
    public PlayerAction parsePlayer(Instruction instruction) throws QuestException {
        String worldName = instruction.string().get().getValue(null);
        String direction = instruction.string().get().getValue(null);
        return new SummonEndCrystalEvent(worldName, direction);
    }
}
