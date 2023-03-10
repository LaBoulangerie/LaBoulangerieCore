package net.laboulangerie.laboulangeriecore.betonquest;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class HasHouseCondition extends Condition {
    @SuppressWarnings("deprecation")
    public HasHouseCondition(Instruction instruction) {
        super(instruction);
    }

    @Override
    protected Boolean execute(String playerId) throws QuestRuntimeException {
        Resident resident = TownyUniverse.getInstance().getResidentOpt(UUID.fromString(playerId)).orElse(null);
        if (resident == null) return false;
        Nation nation = resident.getNationOrNull();
        if (nation == null) return false;
        return LaBoulangerieCore.nationHouseHolder.hasHouse(nation.getUUID());
    }
}
