package net.laboulangerie.laboulangeriecore.core.houses;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class HousesManager {
    private final File dataFolder;
    private final Map<UUID, House> houses = new HashMap<>();

    public HousesManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void loadHouses() throws IOException, ClassNotFoundException {
        if (!dataFolder.exists()) dataFolder.mkdirs();
        else {
            File[] saves = dataFolder.listFiles((dir, file) -> file.endsWith(".yml"));
            for (File save : saves) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(save);
                House house = (House) yml.get("house");
                houses.put(house.getUUID(), house);
            }
        }
    }

    public void saveHouses() throws IOException {
        if (!dataFolder.exists()) dataFolder.mkdirs();
        for (House house : houses.values()) {
            File file = new File(dataFolder, house.getUUID() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

            yml.set("house", house);
            yml.save(file);
        }
    }

    public void deleteHouse(UUID houseId) {
        houses.remove(houseId);
        File file = new File(dataFolder, houseId + ".yml");
        if (file.exists())
            file.delete();
    }

    public Optional<House> getHouseByName(@NotNull String name) {
        return (houses.values().stream().filter(house -> house.getName().equals(name)).findFirst());
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Map<UUID, House> getHouses() {
        return houses;
    }

    public House getHouse(UUID houseId) {
        return houses.get(houseId);
    }
    
    public boolean hasHouse(UUID houseId) {
        return houses.containsKey(houseId);
    }

    public void addHouse(House house) {
        houses.put(house.getUUID(), house);
    }

    public Optional<House> getHouseAt(Location loc) {
        return houses.values().stream().parallel().filter(house -> house.hasBlock(loc)).findAny();
    }
}