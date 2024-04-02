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
import net.laboulangerie.laboulangeriecore.core.nametag.PlayerNameTag;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;

public class CoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 1)
            return false;

        if (Arrays.asList("reload", "rl").contains(args[0].toString())) {
            sender.sendMessage("§bReloading config...");
            LaBoulangerieCore.PLUGIN.reloadConfig();
            UsersData.init(); // Clean cache & ensure directory exists
            sender.sendMessage("§bReloading speed paths...");
            LaBoulangerieCore.PLUGIN.getSpeedPathManager().clear();
            LaBoulangerieCore.PLUGIN.getSpeedPathManager().load();
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

        if (args[0].equalsIgnoreCase("nametag") && args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("§4Player is offline!");
                return true;
            }
            PlayerNameTag tag = PlayerNameTag.get(target);

            if (args.length == 2) {
                sender.sendMessage("Debugging " + target.getName() + "'s nametag");
                sender.sendMessage("* Number of viewers: §e" + tag.getViewers().size());
                sender.sendMessage("* Viewers:");
                for (Player p : tag.getViewers()) {
                    sender.sendMessage("  - §e" + p.getName());
                }
                return true;
            } else {
                Player secondPlayer = null;
                switch (args[2].toLowerCase()) {
                    case "hide":

                        break;

                    default:
                        if (args.length >= 4) {
                            secondPlayer = Bukkit.getPlayer(args[3]);
                            if (secondPlayer == null || !secondPlayer.isOnline()) {
                                sender.sendMessage("§4Player isn't online");
                                return true;
                            }
                        } else
                            return false;
                    case "addViewer":
                        tag.addViewer(secondPlayer);
                        return true;
                    case "removeViewer":
                        tag.removeViewer(secondPlayer);
                        return true;
                    case "sendNametag":
                        tag.sendEntities(secondPlayer);
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");
        if (args.length == 1)
            suggestions = Arrays.asList("reload", "rl", "conversion", "nick", "unnick", "spawnDragon", "nametag");
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
