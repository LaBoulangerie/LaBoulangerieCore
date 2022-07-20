package net.laboulangerie.laboulangeriecore.eastereggs.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class eEggFileUtil {
    public static YamlConfiguration eggsData;
    public static File eggsFile = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "/eggs.yml");

    public static void ensureFilesExist() throws IOException {
        File folder = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "/eastereggs");
        if (!folder.exists()) folder.mkdir();

        if (!eggsFile.exists()) eggsFile.createNewFile();
        eggsData = YamlConfiguration.loadConfiguration(eggsFile);
        if (!eggsData.isSet("eggs")) eggsData.set("eggs", new ArrayList<String>());
    }

    /**Here I check if the player file exists*/
    public static void fileExist(Player player) throws IOException {
        File file = new File(LaBoulangerieCore.PLUGIN.getDataFolder() + "/eastereggs/"+player.getUniqueId()+".yml");
        if(!file.exists()){
            file.createNewFile();
        }
    }

    public static File getPlayerFile(Player player) {
        return new File(LaBoulangerieCore.PLUGIN.getDataFolder() + "/eastereggs/" + player.getUniqueId() + ".yml");
    }
}
