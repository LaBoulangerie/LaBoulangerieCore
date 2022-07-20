package net.laboulangerie.laboulangeriecore.favors;

import org.bukkit.OfflinePlayer;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class DivineFavorsHolder {
    /**
     * @param player
     * @return the player's divine favors bank id
     */
    public static String getOrCreate(OfflinePlayer player) {
        String bankId = "divinefavors-" + player.getUniqueId();

        if (!LaBoulangerieCore.econ.hasAccount(bankId)) {
            LaBoulangerieCore.econ.createPlayerAccount(bankId);
        }
        return bankId;
    }

    /**
     * Get the value of divine favors of the player
     * @param player
     * @return
     */
    public static double getDivineFavorsAmount(OfflinePlayer player) {
        return LaBoulangerieCore.econ.getBalance(getOrCreate(player));
    }
    /**
     * Give {@code amount} of divine favors to the player
     * @param player
     * @param amount
     */
    public static void giveDivineFavors(OfflinePlayer player, double amount) {
        LaBoulangerieCore.econ.depositPlayer(getOrCreate(player), amount);
    }

    /**
     * Withdraw if possible {@code amount} of divine favors
     * @param player
     * @param amount
     * @return false if the player doesn't have enough favors, true otherwise
     */
    public static boolean withdrawDivineFavors(OfflinePlayer player, double amount) {
        if (LaBoulangerieCore.econ.has(getOrCreate(player), amount)) {
            LaBoulangerieCore.econ.withdrawPlayer(getOrCreate(player), amount);
            return true;
        }
        return false;
    }
}
