package net.laboulangerie.laboulangeriecore.betonquest;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class KingCondition extends Condition {
    @SuppressWarnings("deprecation")
    public KingCondition(Instruction instruction) {
        super(instruction);
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        Resident resident = TownyUniverse.getInstance().getResident(UUID.fromString(playerID));
        return resident != null && resident.isKing();
    }
}
