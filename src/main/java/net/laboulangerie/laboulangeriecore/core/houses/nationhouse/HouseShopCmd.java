package net.laboulangerie.laboulangeriecore.core.houses.nationhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.core.houses.House;

public class HouseShopCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            HouseShop.displayShop(player, (short) 0);
            YamlConfiguration data =
                    UsersData.get((Player) sender).orElseGet(() -> UsersData.createUserData((Player) sender));
            data.set("houseshop-uses", data.get("houseshop-uses", 0));
            return true;
        }
        if (args[0].equalsIgnoreCase("sell")) {
            Nation nation = null;
            Resident resident = null;
            try {
                resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
                nation = resident.getNation();
            } catch (TownyException e) {
            } finally {
                if (nation == null) {
                    player.sendMessage("§cVous n'avez pas de nation !");
                    return true;
                }
            }
            if (!LaBoulangerieCore.nationHouseHolder.hasHouse(nation.getUUID())) {
                player.sendMessage("§cVotre nation n'a pas de maison de nation !");
                return true;
            }
            if (!resident.isMayor()) {
                player.sendMessage("§cVous devez être roi de la nation pour faire ça !");
                return true;
            }
            House house = LaBoulangerieCore.housesManager
                    .getHouse(LaBoulangerieCore.nationHouseHolder.getHouseOfNation(nation.getUUID()));
            LaBoulangerieCore.nationHouseHolder.freeHouse(house.getUUID());

            player.sendMessage("§aVotre maison de nation a été vendue.");
            house.getMembers().clear();
            YamlConfiguration data =
                    UsersData.get((Player) sender).orElseGet(() -> UsersData.createUserData((Player) sender));
            data.set("houses-sold", data.get("houses-sold", 0));
            return true;
        }
        return true;
    }
}
