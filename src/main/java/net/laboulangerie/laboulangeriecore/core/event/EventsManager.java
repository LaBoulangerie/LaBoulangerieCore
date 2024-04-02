package net.laboulangerie.laboulangeriecore.core.event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class EventsManager {
    private static File file = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "events.yml");
    private static Map<String, EventState> events = new HashMap<>();

    public static void innit() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LaBoulangerieCore.PLUGIN.getLogger().severe("Unable to create events.yml file: " + e.getMessage());
            }
            return;
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        for (String key : conf.getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection(key);
            EventState eventState = new EventState(key);

            for (Map<?, ?> step : section.getMapList("steps")) {
                @SuppressWarnings("unchecked")
                Map<String, ?> castStep = (Map<String, ?>) step;
                @SuppressWarnings("unchecked")
                EventStep eventStep = new EventStep(key, (String) castStep.get("name"),
                        (ArrayList<String>) castStep.get("actions"));
                eventState.addStep(eventStep);
            }
            events.put(key, eventState);
        }
    }

    public static boolean hasEvent(String name) {
        return events.containsKey(name);
    }

    public static EventState getEvent(String name) {
        return events.get(name);
    }

    public static List<String> getEvents() {
        return List.copyOf(events.keySet());
    }
}
