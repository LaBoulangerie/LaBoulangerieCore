package net.laboulangerie.laboulangeriecore.core.houses;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HouseFlagCmd implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 2 || !Arrays.asList("list", "add", "remove").contains(args[1].toLowerCase())) return false;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseByName(args[0].replaceAll("_", " "));

        if (!house.isPresent()) {
            sender.sendMessage("§4No house named: " + args[0]);
            return false;
        }

        if (args[1].equalsIgnoreCase("list")) {
            sender.sendMessage("§a" + house.get().getName() + ", ("+ house.get().getUUID() +") :");
            house.get().getFlags().forEach(flag -> sender.sendMessage("§b- " + flag.toString()));
            return true;
        }

        if (args.length < 3) return false;
        HouseFlags flag;

        try { flag = HouseFlags.valueOf(args[2]); }
        catch (Exception e) {
            sender.sendMessage("§4Invalid flag: " + args[2]);
            return true;
        }

        if (args[1].equalsIgnoreCase("add")) {
            if (house.get().getFlags().contains(flag)) {
                sender.sendMessage("§4House already contains flag: " + args[2]);
                return true;
            }
            house.get().getFlags().add(flag);
            sender.sendMessage("§aFlag added.");
            return true;
        }

        if (args[1].equalsIgnoreCase("remove")) {
            if (house.get().getFlags().remove(flag))
                sender.sendMessage("§aFlag removed.");
            else
                sender.sendMessage("§4House doesn't contain flag: " + args[2]);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");

        if (args.length == 1) {
            suggestions = LaBoulangerieCore.housesManager.getHouses().values()
                .stream().map(house -> house.getName().replaceAll(" ", "_")).collect(Collectors.toList());
        }else if (args.length == 2) {
            suggestions = Arrays.asList("add", "remove", "list");
        }else if (args.length == 3 && !args[1].equalsIgnoreCase("list")) {
            suggestions = Arrays.asList(HouseFlags.values()).stream().map(HouseFlags::toString).collect(Collectors.toList());
        }

        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }
}
