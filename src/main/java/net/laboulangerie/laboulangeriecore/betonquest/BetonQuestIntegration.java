package net.laboulangerie.laboulangeriecore.betonquest;

import java.util.logging.Logger;
import org.betonquest.betonquest.BetonQuest;

public class BetonQuestIntegration {

    private static Logger logger;

    public static void register(Logger pluginLogger) {
        logger = pluginLogger;
        try {
            BetonQuest.getInstance()
                .getBetonQuestApi()
                .actions()
                .registry()
                .register("lcore_summon_end_crystal", new SummonEndCrystalEventFactory());
            logger.info("BetonQuest integration registered: lcore_summon_end_crystal");
        } catch (Exception e) {
            logger.severe("Failed to register BetonQuest integration: " + e.getMessage());
        }
    }
}
