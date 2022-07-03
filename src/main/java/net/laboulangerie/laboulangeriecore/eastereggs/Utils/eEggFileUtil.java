package net.laboulangerie.laboulangeriecore.eastereggs.Utils;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class eEggFileUtil {

    /**Here I check if the folder exists*/
    public static void createFolder(){
        File folder = new File("plugins/LaboulangerieCore/PlayerData");
        if(!folder.exists()){
            folder.mkdir();
        }else System.out.println("Folder exists");
    }

    /**Here I check if the player file exists*/
    public static void fileExist(Player player) throws IOException {
        File file = new File("plugins/LaBoulangerieCore/PlayerData/"+player.getName()+".yml");
        if(!file.exists()){
            file.createNewFile();
        }
    }

}
