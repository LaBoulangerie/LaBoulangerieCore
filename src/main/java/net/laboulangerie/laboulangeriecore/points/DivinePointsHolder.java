package net.laboulangerie.laboulangeriecore.points;

import org.bukkit.OfflinePlayer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class DivinePointsHolder {
    /**
     * @param player
     * @return the player's divine points bank id
     */
    public static String getOrCreate(OfflinePlayer player) {
        String bankId = "divinepoints-" + player.getUniqueId();

        if (!LaBoulangerieCore.econ.hasAccount(bankId)) {
            LaBoulangerieCore.econ.createPlayerAccount(bankId);
        }
        return bankId;
    }

    /**
     * Get the value of divine points of the player
     * @param player
     * @return
     */
    public static double getDivinePointsAmount(OfflinePlayer player) {
        return LaBoulangerieCore.econ.getBalance(getOrCreate(player));
    }
    /**
     * Give {@code amount} of divine points to the player
     * @param player
     * @param amount
     */
    public static void giveDivinePoints(OfflinePlayer player, double amount) {
        LaBoulangerieCore.econ.depositPlayer(getOrCreate(player), amount);
    }

    /**
     * Withdraw if possible {@code amount} of divine points
     * @param player
     * @param amount
     * @return false if the player doesn't have enough points, true otherwise
     */
    public static boolean withdrawDivinePoints(OfflinePlayer player, double amount) {
        if (LaBoulangerieCore.econ.has(getOrCreate(player), amount)) {
            LaBoulangerieCore.econ.withdrawPlayer(getOrCreate(player), amount);
            return true;
        }
        return false;
    }
}
