package net.laboulangerie.laboulangeriecore.betonquest;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class RankCondition extends Condition {
    @SuppressWarnings("deprecation")
    public RankCondition(Instruction instruction) {
        super(instruction);
    }

    @Override
    protected Boolean execute(String playerId) throws QuestRuntimeException {
        Resident resident = TownyUniverse.getInstance().getResident(UUID.fromString(playerId));
        if (resident == null)
            return false;
        try {
            switch (instruction.getPart(1)) {
                case "nation":
                    return resident.hasNationRank(instruction.getPart(2));
                case "town":
                    return resident.hasTownRank(instruction.getPart(2));
                default:
                    throw new QuestRuntimeException(
                            "Invalid argument: " + instruction.getPart(1) + ", possible arguments: town & nation");
            }
        } catch (InstructionParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
