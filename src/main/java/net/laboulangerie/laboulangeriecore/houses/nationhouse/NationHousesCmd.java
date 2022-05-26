package net.laboulangerie.laboulangeriecore.houses.nationhouse;

import java.util.Arrays;
import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.houses.House;

public class NationHousesCmd implements CommandExecutor {
    public NationHousesCmd() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (args.length >= 2 && args[0].equalsIgnoreCase("list") && Arrays.asList("free", "occupied").contains(args[1])) {
            short pageToDisplay = 0;
            if (args.length >= 3) {
                try {
                    pageToDisplay = Short.parseShort(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§4Unable to parse §e" + args[2] + "§4 to type short (numeric)!");
                    return true;
                }
            }
            if (pageToDisplay * 8 > (args[1].equals("free") ? LaBoulangerieCore.PLUGIN.nationHouseHolder.getFreeHouses().size() : LaBoulangerieCore.PLUGIN.nationHouseHolder.getOccupiedHouses().size())) {
                sender.sendMessage("§4There is no page " + pageToDisplay + " to display!");
                return true;
            }
            sender.sendMessage("§2-----§a[§epage " + (pageToDisplay+1) + "§a]§2-----");
            if (args[1].equals("free")) {
                for (int i = pageToDisplay*8; i < LaBoulangerieCore.PLUGIN.nationHouseHolder.getFreeHouses().size() && i < (pageToDisplay+1)*8; i++) {
                    UUID id = LaBoulangerieCore.PLUGIN.nationHouseHolder.getFreeHouses().get(i);
                    House house = LaBoulangerieCore.PLUGIN.housesManager.getHouse(id);
                    sender.sendMessage("§2" + house.getName() + " §r--- §3" + LaBoulangerieCore.PLUGIN.nationHouseHolder.getHousePrice(id) + "$");
                }
            }else {
                for (int i = pageToDisplay*8; i < LaBoulangerieCore.PLUGIN.nationHouseHolder.getOccupiedHouses().size() && i < (pageToDisplay+1)*8; i++) {
                    UUID id = (UUID) LaBoulangerieCore.PLUGIN.nationHouseHolder.getOccupiedHouses().keySet().toArray()[i];
                    House house = LaBoulangerieCore.PLUGIN.housesManager.getHouse(id);
                    sender.sendMessage("§5" + house.getName() + " §r--- §3" + TownyUniverse.getInstance().getNation(id));
                }
            }
            return true;
        }
        return false;
    }
}
