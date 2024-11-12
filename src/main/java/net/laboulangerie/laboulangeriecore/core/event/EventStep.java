package net.laboulangerie.laboulangeriecore.core.event;

import java.util.ArrayList;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class EventStep {
    private String parentEvent;
    private String name;
    private ArrayList<String> actions = new ArrayList<>();

    public EventStep(String parentName, String name, ArrayList<String> actions) {
        parentEvent = parentName;
        this.name = name;
        this.actions = actions;
    }

    public void executeActions(Player executor) {
        int i = -1;
        for (String action : actions) {
            i++;
            String[] params = action.split(" ", 2);
            if (params.length < 2) {
                LaBoulangerieCore.PLUGIN.getLogger()
                        .warning("Invalid action in: " + parentEvent + ":" + name + ":" + i);
                continue;
            }

            switch (params[0]) {
                case "cmd":
                    Bukkit.getCommandMap().dispatch(Bukkit.getConsoleSender(), params[1]);
                    break;
                case "bq_event":
                    String identifier = params[1].split("\\.")[0];
                    String packageDir = identifier.replaceAll("-", "/");
                    try {
                        BetonQuest.event(
                            PlayerConverter.getID(executor),
                            new EventID(Config.getPackages().get(packageDir), identifier)
                        );
                    } catch (ObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }

    public String getName() {
        return name;
    }
}
