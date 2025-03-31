package net.laboulangerie.laboulangeriecore.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class RankCondition implements PlayerCondition {
    private String entity;
    private String rank;
    /**
     * Checks if the player has the specified rank in the specified entity
     * @param entity nation or town
     * @param rank assistant, co-maire...
     */
    public RankCondition(String entity, String rank) {
        this.entity = entity;
        this.rank = rank;
    }

    @Override
    public boolean check(Profile profile) throws QuestRuntimeException {
        /*Resident resident = TownyUniverse.getInstance().getResident(profile.getPlayerUUID());
        if (resident == null)
            return false;
        switch (entity) {
            case "nation":
                return resident.hasNationRank(rank);
            case "town":
                return resident.hasTownRank(rank);
            default:
                throw new QuestRuntimeException("Invalid argument: "
                    + entity + ", possible arguments: town & nation");
        }*/

        return false;
    }
}
