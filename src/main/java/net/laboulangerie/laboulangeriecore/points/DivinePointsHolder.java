package net.laboulangerie.laboulangeriecore.points;

import org.bukkit.OfflinePlayer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class DivinePointsHolder {
    /**
     * @param player
     * @return the player's divine points bank id
     */
    public static String getOrCreate(OfflinePlayer player) {
        String bankId = "divinepoints-" + player.getUniqueId();

        if (!LaBoulangerieCore.econ.getBanks().contains(bankId)) {
            LaBoulangerieCore.econ.createBank(bankId, player);
        }
        return bankId;
    }

    /**
     * Get the value of divine points of the player
     * @param player
     * @return
     */
    public static double getDivinePointsAmount(OfflinePlayer player) {
        return LaBoulangerieCore.econ.bankBalance(getOrCreate(player)).amount;
    }
    /**
     * Give {@code amount} of divine points to the player
     * @param player
     * @param amount
     */
    public static void giveDivinePoints(OfflinePlayer player, double amount) {
        LaBoulangerieCore.econ.bankDeposit(getOrCreate(player), amount);
    }

    /**
     * Withdraw if possible {@code amount} of divine points
     * @param player
     * @param amount
     * @return false if the player doesn't have enough points, true otherwise
     */
    public static boolean withdrawDivinePoints(OfflinePlayer player, double amount) {
        if (LaBoulangerieCore.econ.bankHas(getOrCreate(player), amount).type == ResponseType.SUCCESS) {
            LaBoulangerieCore.econ.bankWithdraw(getOrCreate(player), amount);
            return true;
        }
        return false;
    }
}
