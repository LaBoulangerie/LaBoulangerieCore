package net.laboulangerie.laboulangeriecore.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.core.end.Dragon;
import net.laboulangerie.laboulangeriecore.core.event.EventsManager;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;
import net.laboulangerie.laboulangeriecore.misc.VaultsReset;

public class CoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 1)
            return false;

        if (Arrays.asList("reload", "rl").contains(args[0].toString())) {
            sender.sendMessage("§bReloading config...");
            LaBoulangerieCore.PLUGIN.reloadConfig();
            UsersData.init(); // Cleans cache & ensures directory exists
            sender.sendMessage("§bReloading speed paths...");
            LaBoulangerieCore.PLUGIN.getSpeedPathManager().clear();
            LaBoulangerieCore.PLUGIN.getSpeedPathManager().load();
            sender.sendMessage("§bReloading events assistant...");
            EventsManager.innit();
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("conversion")) {
            if (!(sender instanceof Player))
                sender.sendMessage("§4Only players can use that");
            else {
                ConversionInv.displayConversionInv((Player) sender);
                YamlConfiguration data = UsersData.getOrCreate((Player) sender);
                data.set("conversions-count", data.getInt("conversions-count", 0) + 1);
                try {
                    UsersData.save((Player) sender, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("spawndragon")) {
            World world = Bukkit.getWorld(LaBoulangerieCore.PLUGIN.getConfig().getString("dragon-world"));
            ArrayList<Location> crystalLocs = new ArrayList<>();
            List<Map<?, ?>> confCrystals = LaBoulangerieCore.PLUGIN.getConfig().getMapList("crystals");

            for (Map<?, ?> map : confCrystals) {
                @SuppressWarnings("unchecked")
                Map<String, Double> crystal = (Map<String, Double>) map;
                crystalLocs.add(new Location(world, crystal.get("x"), crystal.get("y"), crystal.get("z")));
            }
            Dragon dragon = new Dragon(
                    new Location(world, 0, LaBoulangerieCore.PLUGIN.getConfig().getDouble("dragon-podium-y"), 0),
                    crystalLocs, LaBoulangerieCore.PLUGIN.getConfig().getInt("dragon-health"));

            dragon.spawn();
            dragon.spawnCrystals();

            if (args.length > 1 && args[1].equalsIgnoreCase("withEgg"))
                dragon.setShouldSpawnEgg(true);
            return true;
        }

        if (args[0].equalsIgnoreCase("nick") && args.length > 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (target == null) {
                sender.sendMessage("§4Player is unknown!");
                return true;
            }
            YamlConfiguration data = UsersData.getOrCreate(target);

            String name = List.of(args).subList(2, args.length).stream().reduce((a, b) -> a + " " + b).get();
            data.set("nick", name);
            try {
                UsersData.save(target, data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (target.isOnline()) {
                ((Player) target).displayName(Component.text(name));
            }
            sender.sendMessage("§aNickname set successfully!");
            return true;
        }

        if (args[0].equalsIgnoreCase("unnick") && args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (target == null) {
                sender.sendMessage("§4Player is unknown!");
                return true;
            }
            YamlConfiguration data = UsersData.getOrCreate(target);
            data.set("nick", null);
            try {
                UsersData.save(target, data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (target.isOnline()) {
                ((Player) target).displayName(null);
            }
            sender.sendMessage("§aNickname reset successfully!");
            return true;
        }

        if (args[0].equalsIgnoreCase("resetvaults")) {
            VaultsReset.reset();
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");
        if (args.length == 1)
            suggestions = Arrays.asList("reload", "rl", "conversion", "nick", "unnick", "spawnDragon", "nametag",
                    "resetvaults");
        if (args.length == 2 && (args[0].equalsIgnoreCase("nick") ||
                args[0].equalsIgnoreCase("unnick") ||
                args[0].equalsIgnoreCase("nametag")))
            return null;
        if (args.length == 3 && args[0].equalsIgnoreCase("nametag"))
            suggestions = Arrays.asList("addViewer", "removeViewer", "sendNametag");

        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length - 1]))
                .collect(Collectors.toList());
    }
}
