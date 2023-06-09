package net.laboulangerie.laboulangeriecore.speedpaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class SpeedPathManager {

    private Map<String, SpeedPath> paths = new HashMap<>();

    public void load() {
        ConfigurationSection pathsConfig = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("speed-paths");

        for (String pathKey : pathsConfig.getValues(false).keySet()) {
            ConfigurationSection pathConfig = pathsConfig.getConfigurationSection(pathKey);
            SpeedPath path = loadPathFromConfig(pathConfig);
            paths.put(pathKey, path);
        }
    }

    private SpeedPath loadPathFromConfig(ConfigurationSection config) {
        List<Material> materials = new ArrayList<>();
        for (String materialString : config.getStringList("blocks")) {
            try {
                materials.add(Material.valueOf(materialString));
            } catch (Exception e) {
                LaBoulangerieCore.PLUGIN.getLogger().warning("Invalid material on speed paths: " + materialString);
                e.printStackTrace();
            }
        }

        float speed = (float) config.getDouble("speed");
        return new SpeedPath(materials, speed);
    }

    public void clear() {
        paths.clear();
    }

    public Map<String, SpeedPath> getPaths() {
        return paths;
    }

    public SpeedPath getPath(String pathKey) {
        return paths.get(pathKey);
    }
}
