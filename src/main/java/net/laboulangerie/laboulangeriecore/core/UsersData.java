package net.laboulangerie.laboulangeriecore.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class UsersData {
    private static File dataFolder = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "users/");
    private static HashMap<UUID, YamlConfiguration> usersData;

    public static void init() {
        if (!dataFolder.exists()) dataFolder.mkdir();
        usersData = new HashMap<>();
    }

    /**
     * Get the user's data if it's present or an empty optional
     * 
     * @param user
     * @return
     */
    public static Optional<YamlConfiguration> get(OfflinePlayer user) {
        if (usersData.containsKey(user.getUniqueId())) return Optional.of(usersData.get(user.getUniqueId()));

        List<String> usersUUIDs = List.of(dataFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml")))
                .stream().map(file -> file.getName().split("\\.")[0]).collect(Collectors.toList());

        if (!usersUUIDs.contains(user.getUniqueId().toString())) return Optional.empty();

        File userFile = new File(dataFolder, user.getUniqueId() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(userFile);
        usersData.put(user.getUniqueId(), data);

        return Optional.of(data);
    }

    /**
     * If this fails to create the user's save file it will return an empty YamlConfiguration and the IO error should be
     * handled whe trying to save this YamlConfiguration
     * 
     * @param user
     * @return
     */
    public static YamlConfiguration createUserData(OfflinePlayer user) {
        File userFile = new File(dataFolder, user.getUniqueId() + ".yml");
        if (userFile.exists()) throw new IllegalArgumentException("This user already has a data file!");

        try {
            userFile.createNewFile();
            YamlConfiguration data = YamlConfiguration.loadConfiguration(userFile);
            usersData.put(user.getUniqueId(), data);
            return data;
        } catch (IOException e) {
            return new YamlConfiguration();
        }
    }

    public static void save(OfflinePlayer user, YamlConfiguration data) throws IOException {
        data.save(new File(dataFolder, user.getUniqueId() + ".yml"));
    }
}
