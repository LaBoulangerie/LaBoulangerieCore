package net.laboulangerie.laboulangeriecore.houses;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HousesManager {
    private final File dataFolder;
    private final Map<UUID, House> houses = new HashMap<>();

    public HousesManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void loadHouses() throws IOException, ClassNotFoundException {
        if (!dataFolder.exists()) dataFolder.mkdirs();
        else {
            File[] saves = dataFolder.listFiles((dir, file) -> file.endsWith(".ho"));
            for (File save : saves) {
                try (FileInputStream file = new FileInputStream(save);
                     ObjectInputStream in = new ObjectInputStream(file)) {

                    House house = (House) in.readObject();
                    houses.put(house.getUUID(), house);
                } catch (IOException | ClassNotFoundException e) {
                    throw e;//We pass the error, the catch is only here to close the streams
                }
            }
        }
    }

    public void saveHouses() throws IOException {
        if (!dataFolder.exists()) dataFolder.mkdirs();
        for (House house : houses.values()) {
            File file = new File(dataFolder, house.getUUID() + ".ho");
            file.createNewFile();
            
            try (FileOutputStream fileOut = new FileOutputStream(file);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                out.writeObject(house);
            } catch (IOException e) {
                throw e;//We pass the error, the catch is only here to close the streams
            }
        }
    }

    public void deleteHouse(UUID houseId) {
        File file = new File(dataFolder, houseId + ".ho");
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

    public void addHouse(UUID houseId, House house) {
        houses.put(houseId, house);
    }

    public void removeHouse(UUID houseId) {
        houses.remove(houseId);
    }
}