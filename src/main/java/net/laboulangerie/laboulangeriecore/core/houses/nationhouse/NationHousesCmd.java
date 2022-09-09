package net.laboulangerie.laboulangeriecore.core.houses.nationhouse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.palmergames.bukkit.towny.TownyUniverse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.houses.House;
import net.laboulangerie.laboulangeriecore.core.houses.HouseFlags;

public class NationHousesCmd implements CommandExecutor, TabCompleter {
    public NationHousesCmd() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (args.length < 2) return false;
        if (args[0].equalsIgnoreCase("list") && Arrays.asList("free", "occupied").contains(args[1])) {
            short pageToDisplay = 0;
            if (args.length >= 3) {
                try {
                    pageToDisplay = Short.parseShort(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§4Unable to parse §e" + args[2] + "§4 to type short (numeric)!");
                    return true;
                }
            }
            if (pageToDisplay * 8 > (args[1].equals("free") ? LaBoulangerieCore.nationHouseHolder.getFreeHouses().size() : LaBoulangerieCore.nationHouseHolder.getOccupiedHouses().size())) {
                sender.sendMessage("§4There is no page " + pageToDisplay + " to display!");
                return true;
            }
            sender.sendMessage("§2-----§a[§epage " + (pageToDisplay) + "§a]§2-----");
            if (args[1].equals("free")) {
                for (int i = pageToDisplay*8; i < LaBoulangerieCore.nationHouseHolder.getFreeHouses().size() && i < (pageToDisplay+1)*8; i++) {
                    UUID id = LaBoulangerieCore.nationHouseHolder.getFreeHouses().get(i);
                    House house = LaBoulangerieCore.housesManager.getHouse(id);
                    sender.sendMessage("§2" + house.getName() + " §r--- §3" + LaBoulangerieCore.nationHouseHolder.getHousePrice(id) + "$");
                }
            } else {
                for (int i = pageToDisplay*8; i < LaBoulangerieCore.nationHouseHolder.getOccupiedHouses().size() && i < (pageToDisplay+1)*8; i++) {
                    UUID id = (UUID) LaBoulangerieCore.nationHouseHolder.getOccupiedHouses().keySet().toArray()[i];
                    House house = LaBoulangerieCore.housesManager.getHouse(id);
                    UUID nationId = LaBoulangerieCore.nationHouseHolder.getOccupiedHouses().get(id);

                    sender.sendMessage("§5" + house.getName() + " §r--- §3" + TownyUniverse.getInstance().getNation(nationId));
                }
            }
            return true;
        }
        args[1] = args[1].replaceAll("_", " ");
        if (args[0].equalsIgnoreCase("create") && args.length > 2) {
            double price = 0;
            try {
                price = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4Unable to parse §e" + args[2] + " §4to type Double (numeric)!");
                return true;
            }
            House house = LaBoulangerieCore.housesManager.getHouseByName(args[1]).orElse(null);
            if (house == null) {
                sender.sendMessage("§4No house named §e" + args[1] + "§4!");
                return true;
            }
            if (LaBoulangerieCore.nationHouseHolder.getHousePrice(house.getUUID()) != null) {
                sender.sendMessage("§4This house is already a house of nation!");
                return true;
            }
            LaBoulangerieCore.nationHouseHolder.newNationHouse(house.getUUID(), price);
            house.addFlag(HouseFlags.CAN_BREAK);
            house.addFlag(HouseFlags.CAN_BUILD);
            house.addFlag(HouseFlags.CAN_SET_ARMOR_STANDS);
            house.addFlag(HouseFlags.CAN_SET_HANGINGS);
            house.addFlag(HouseFlags.CAN_FLICK);
            sender.sendMessage("§2House of nation successfully created!");
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            House house = LaBoulangerieCore.housesManager.getHouseByName(args[1]).orElse(null);
            if (house == null) {
                sender.sendMessage("§4No house named §e" + args[1] + "§4!");
                return true;
            }
            LaBoulangerieCore.nationHouseHolder.deleteNationHouse(house.getUUID());
            sender.sendMessage("§2House of nation successfully deleted!");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");
        if (args.length == 1) suggestions = Arrays.asList("list", "create", "delete");
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) suggestions = Arrays.asList("free", "occupied");
            else if (args[0].equalsIgnoreCase("delete")) {
                suggestions = LaBoulangerieCore.nationHouseHolder.getFreeHouses().stream()
                    .map(id -> LaBoulangerieCore.housesManager.getHouse(id).getName().replaceAll(" ", "_"))
                    .collect(Collectors.toList());

                suggestions.addAll(
                    LaBoulangerieCore.nationHouseHolder.getOccupiedHouses().keySet().stream()
                        .map(id -> LaBoulangerieCore.housesManager.getHouse(id).getName().replaceAll(" ", "_"))
                        .collect(Collectors.toList())
                );
            } else if (args[0].equalsIgnoreCase("create")) {
                suggestions = LaBoulangerieCore.housesManager.getHouses().values()
                    .stream().filter(house -> !LaBoulangerieCore.nationHouseHolder.exists(house)).map(house -> house.getName().replaceAll(" ", "_")).collect(Collectors.toList());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) suggestions = Arrays.asList("100", "500", "1000", "5000");

        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }
}
