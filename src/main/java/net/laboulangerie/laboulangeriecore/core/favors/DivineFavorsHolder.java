package net.laboulangerie.laboulangeriecore.core.favors;

import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class DivineFavorsHolder {
    /**
     * Get the value of divine favors of the player
     * 
     * @param player
     * @return
     */
    public static double getDivineFavorsAmount(OfflinePlayer player) {
        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
        return data.getDouble("divine-favors", 0);
    }

    /**
     * Give {@code amount} of divine favors to the player
     * 
     * @param player
     * @param amount
     */
    public static void giveDivineFavors(OfflinePlayer player, double amount) {
        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
        data.set("divine-favors", data.getDouble("divine-favors", 0) + amount);
        try {
            UsersData.save(player, data);
        } catch (IOException e) {
            LaBoulangerieCore.PLUGIN.getLogger()
                    .severe("An error occurred while trying to save " + player.getName() + "'s divine favors");
            e.printStackTrace();
        }
    }

    /**
     * Withdraw if possible {@code amount} of divine favors
     * 
     * @param player
     * @param amount
     * @return false if the player doesn't have enough favors, true otherwise
     */
    public static boolean withdrawDivineFavors(OfflinePlayer player, double amount) {
        YamlConfiguration data = UsersData.get(player).orElseGet(() -> UsersData.createUserData(player));
        if (data.getDouble("divine-favors", 0) >= amount) {
            data.set("divine-favors", data.getDouble("divine-favors") - amount);
            try {
                UsersData.save(player, data);
            } catch (IOException e) {
                LaBoulangerieCore.PLUGIN.getLogger()
                        .severe("An error occurred while trying to save " + player.getName() + "'s divine favors");
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
