package net.laboulangerie.laboulangeriecore.core.houses.nationhouse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.houses.House;

public class NationHouseHolder {
    private File dataFile = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "nationshouses.yml");
    /**
     * UUID is house's one
     */
    private List<UUID> freeHouses = new ArrayList<>();
    /**
     * First UUID is house, second is nation one
     */
    private Map<UUID, UUID> occupiedHouses = new HashMap<>();
    private Map<UUID, Double> prices = new HashMap<>();

    public NationHouseHolder() {}

    public void loadData() throws IOException {
        if (!dataFile.exists()) {
            dataFile.createNewFile();
            return;
        }

        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        if (data.getList("free-houses") != null) {
            freeHouses = (List<UUID>) data.getStringList("free-houses").stream().map(UUID::fromString).collect(Collectors.toList());
        } else data.createSection("free-houses");

        if (data.contains("occupied-houses")) {
            for (String houseId : data.getConfigurationSection("occupied-houses").getKeys(false)) {
                occupiedHouses.put(UUID.fromString(houseId), UUID.fromString(data.getString("occupied-houses." + houseId)));
            }
        } else data.createSection("occupied-houses");

        if (data.contains("prices")) {
            for (String houseId : data.getConfigurationSection("prices").getKeys(false)) {
                prices.put(UUID.fromString(houseId), data.getDouble("prices." + houseId));
            }
        } else data.createSection("prices");
    }

    public void saveData() throws IOException {
        if (!dataFile.exists()) dataFile.createNewFile();

        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        data.set("free-houses", freeHouses.stream().map(id -> id.toString()).collect(Collectors.toList()));
        data.set("occupied-houses", occupiedHouses.entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey().toString(), e -> e.getValue().toString()
        )));
        data.set("prices", prices.entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey().toString(), Map.Entry::getValue
        )));
        data.save(dataFile);
    }

    public List<UUID> getFreeHouses() { return freeHouses; }
    public Map<UUID, UUID> getOccupiedHouses() { return occupiedHouses; }

    public void newNationHouse(UUID id, Double price) {
        prices.put(id, price);
        freeHouses.add(id);
    }
    /**
     * Assign a house to a nation
     * @param houseId
     * @param nationId
     * @throws IllegalStateException if the house is already assigned
     */
    public void assignNationHouse(UUID houseId, UUID nationId) {
        if (occupiedHouses.containsKey(houseId)) throw new IllegalStateException("House is already assigned to a nation!");
        occupiedHouses.put(houseId, nationId);
        freeHouses.remove(houseId);
    }
    public void freeHouse(UUID houseId) {
        if (freeHouses.contains(houseId)) throw new IllegalStateException("House is already free!");
        occupiedHouses.remove(houseId);
        freeHouses.add(houseId);
    }
    public void deleteNationHouse(UUID houseId) {
        occupiedHouses.remove(houseId);
        freeHouses.remove(houseId);
        prices.remove(houseId);
    }
    public Double getHousePrice(UUID id) {
        return prices.get(id);
    }

    /**
     * Test if nation has an nation house
     * @param nationId the nation to check if it has an house
     * @return
     */
    public boolean hasHouse(UUID nationId) {
        return occupiedHouses.containsValue(nationId);
    }
    /**
     * Check if this house is also an house of nation
     * @param house
     * @return
     */
    public boolean exists(House house) {
        return occupiedHouses.containsKey(house.getUUID()) || freeHouses.contains(house.getUUID());
    }
    public UUID getHouseOfNation(UUID nationId) {
        if (!hasHouse(nationId)) return null;
        return occupiedHouses.entrySet()
            .stream()
            .filter(entry -> nationId.equals(entry.getValue()))
            .map(Map.Entry::getKey).findFirst().get();
    }
}
