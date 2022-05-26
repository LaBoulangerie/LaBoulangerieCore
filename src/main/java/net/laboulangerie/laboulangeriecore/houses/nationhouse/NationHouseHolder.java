package net.laboulangerie.laboulangeriecore.houses.nationhouse;

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
        }else data.createSection("free-houses");

        if (data.getList("occupied-houses") != null) {
            for (String houseId : data.getConfigurationSection("occupied-houses").getKeys(false)) {
                occupiedHouses.put(UUID.fromString(houseId), UUID.fromString(data.getString("occupied-houses." + houseId)));
            }
        }else data.createSection("occupied-houses");
    }

    public void saveData() throws IOException {
        if (!dataFile.exists()) dataFile.createNewFile();

        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        data.set("free-houses", freeHouses);
        data.set("occupied-houses", occupiedHouses);
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
}
