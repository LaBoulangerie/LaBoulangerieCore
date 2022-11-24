package net.laboulangerie.laboulangeriecore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.core.end.Dragon;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;

public class CoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1) return false;

        if (Arrays.asList("reload", "rl").contains(args[0].toString())) {
            sender.sendMessage("§bReloading config...");
            LaBoulangerieCore.PLUGIN.reloadConfig();
            UsersData.init(); // Clean cache & ensure directory exists
            sender.sendMessage("§bReloading name tags...");
            LaBoulangerieCore.PLUGIN.getNameTagManager().disable();
            LaBoulangerieCore.PLUGIN.getNameTagManager().enable();
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("conversion")) {
            if (!(sender instanceof Player))
                sender.sendMessage("§4Only players can use that");
            else {
                ConversionInv.displayConversionInv((Player) sender);
                YamlConfiguration data = UsersData.get((Player) sender).orElseGet(() -> UsersData.createUserData((Player) sender));
                data.set("conversions-count", data.get("conversions-count", 0));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("spawndragon")) {
            List<Location> crystalLocs = new ArrayList<>();
            List<Map<?, ?>> confCrystals = LaBoulangerieCore.PLUGIN.getConfig().getMapList("crystals");

            for (Map<?, ?> map : confCrystals) {
                Map<String, Double> crystal = (Map<String, Double>) map;
                crystalLocs.add(new Location(Bukkit.getWorld("world_the_end"), crystal.get("x"), crystal.get("y"), crystal.get("z")));
            }
            Dragon dragon = new Dragon(new Location(Bukkit.getWorld("world_the_end"), 0, 63, 0),(Location[]) crystalLocs.toArray());
            dragon.spawn();
            dragon.spawnCrystals();
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        
        return null;
    }
}
