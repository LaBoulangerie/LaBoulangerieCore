package net.laboulangerie.laboulangeriecore.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;
import net.laboulangerie.laboulangeriecore.elytra.ElytraManager;
import net.laboulangerie.laboulangeriecore.elytra.StrongWindScheduler;
import net.laboulangerie.laboulangeriecore.misc.VaultsReset;

public class CoreCommand implements TabExecutor {

    private final MiniMessage mm = MiniMessage.miniMessage();
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

        if (args[0].equalsIgnoreCase("elytra")) {
            handleElytra(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        return false;
    }

    private void handleElytra(CommandSender sender, String[] args) {
        ElytraManager elytraManager = LaBoulangerieCore.PLUGIN.getElytraManager();

        if (args.length == 0) {
            sendElytraUsage(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "disable" -> handleElytraDisable(sender, args, elytraManager);
            case "enable" -> handleElytraEnable(sender, args, elytraManager);
            case "status" -> handleElytraStatus(sender, args, elytraManager);
            case "wind" -> handleElytraWind(sender, Arrays.copyOfRange(args, 1, args.length));
            default -> sendElytraUsage(sender);
        }
    }

    private void handleElytraDisable(CommandSender sender, String[] args, ElytraManager elytraManager) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Usage: /lcore elytra disable <monde> [durée]"));
            return;
        }

        String worldName = args[1];
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            sender.sendMessage(mm.deserialize("<red>Monde introuvable: " + worldName));
            return;
        }

        if (args.length >= 3) {
            long durationMillis = parseDuration(args[2]);
            if (durationMillis <= 0) {
                sender.sendMessage(mm.deserialize("<red>Durée invalide. Utilisez: 30s, 5m, 2h"));
                return;
            }

            elytraManager.restrictWorld(worldName, durationMillis);
            String formatted = formatDuration(elytraManager.getRemainingTime(worldName));
            sender.sendMessage(mm.deserialize("<green>Elytras désactivées dans <yellow>" + worldName +
                    "</yellow> pour <yellow>" + formatted + "</yellow>."));
        } else {
            elytraManager.restrictWorldIndefinitely(worldName);
            sender.sendMessage(mm.deserialize("<green>Elytras désactivées dans <yellow>" + worldName +
                    "</yellow> indéfiniment."));
        }
    }

    private void handleElytraEnable(CommandSender sender, String[] args, ElytraManager elytraManager) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Usage: /lcore elytra enable <monde>"));
            return;
        }

        String worldName = args[1];

        if (!elytraManager.isRestricted(worldName)) {
            sender.sendMessage(mm.deserialize("<yellow>Les elytras ne sont pas restreintes dans ce monde."));
            return;
        }

        elytraManager.unrestrictWorld(worldName);
        sender.sendMessage(mm.deserialize("<green>Elytras réactivées dans <yellow>" + worldName + "</yellow>."));
    }

    private void handleElytraStatus(CommandSender sender, String[] args, ElytraManager elytraManager) {
        if (args.length >= 2) {
            String worldName = args[1];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                sender.sendMessage(mm.deserialize("<red>Monde introuvable: " + worldName));
                return;
            }

            sendWorldStatus(sender, world, elytraManager);
        } else {
            Map<String, Long> restricted = elytraManager.getRestrictedWorlds();

            if (restricted.isEmpty()) {
                sender.sendMessage(mm.deserialize("<green>Aucune restriction d'elytra active."));
            } else {
                sender.sendMessage(mm.deserialize("<gold>Restrictions d'elytra actives:"));
                for (Map.Entry<String, Long> entry : restricted.entrySet()) {
                    String worldName = entry.getKey();
                    long remaining = elytraManager.getRemainingTime(worldName);
                    String timeStr = remaining == Long.MAX_VALUE ? "indéfini" : formatDuration(remaining);
                    sender.sendMessage(mm.deserialize("  <gray>- <yellow>" + worldName + "</yellow>: " + timeStr));
                }
            }

            sender.sendMessage(mm.deserialize("<gray>Utilisez <white>/lcore elytra status <monde></white> pour plus de détails."));
        }
    }

    private void sendWorldStatus(CommandSender sender, World world, ElytraManager elytraManager) {
        String worldName = world.getName();
        boolean commandRestricted = elytraManager.isRestricted(worldName);
        boolean windRestricted = elytraManager.isWindRestricted(worldName);
        boolean raining = world.hasStorm();
        boolean rainRestrictionEnabled = elytraManager.isRainRestrictionEnabled();
        boolean windRestrictionEnabled = elytraManager.isWindRestrictionEnabled();

        sender.sendMessage(mm.deserialize("<gold>Statut des elytras pour <yellow>" + worldName + "</yellow>:"));
        sender.sendMessage(mm.deserialize("  <gray>Restriction commande: " +
                (commandRestricted ? "<red>Oui" : "<green>Non")));

        if (commandRestricted) {
            long remaining = elytraManager.getRemainingTime(worldName);
            String timeStr = remaining == Long.MAX_VALUE ? "indéfini" : formatDuration(remaining);
            sender.sendMessage(mm.deserialize("  <gray>Temps restant: <yellow>" + timeStr));
        }

        sender.sendMessage(mm.deserialize("  <gray>Vent fort: " +
                (windRestricted ? "<red>Oui" : "<green>Non")));

        if (windRestricted) {
            long remaining = elytraManager.getRemainingTime(worldName);
            String timeStr = formatDuration(remaining);
            sender.sendMessage(mm.deserialize("  <gray>Temps restant (vent): <yellow>" + timeStr));
        }

        sender.sendMessage(mm.deserialize("  <gray>Pluie active: " +
                (raining ? "<yellow>Oui" : "<green>Non")));
        sender.sendMessage(mm.deserialize("  <gray>Restriction pluie: " +
                (rainRestrictionEnabled ? "<yellow>Activée" : "<gray>Désactivée")));
        sender.sendMessage(mm.deserialize("  <gray>Restriction vent: " +
                (windRestrictionEnabled ? "<yellow>Activée" : "<gray>Désactivée")));
    }

    private void handleElytraWind(CommandSender sender, String[] args) {
        StrongWindScheduler windScheduler = LaBoulangerieCore.PLUGIN.getStrongWindScheduler();
        ElytraManager elytraManager = LaBoulangerieCore.PLUGIN.getElytraManager();

        if (args.length == 0) {
            sendWindUsage(sender);
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(mm.deserialize("<red>Usage: /lcore elytra wind start <monde> [durée]"));
                    return;
                }

                String worldName = args[1];
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    sender.sendMessage(mm.deserialize("<red>Monde introuvable: " + worldName));
                    return;
                }

                if (windScheduler.isWindActive(worldName)) {
                    sender.sendMessage(mm.deserialize("<yellow>Le vent est déjà actif dans ce monde."));
                    return;
                }

                long durationMillis;
                if (args.length >= 3) {
                    durationMillis = parseDuration(args[2]);
                    if (durationMillis <= 0) {
                        sender.sendMessage(mm.deserialize("<red>Durée invalide. Utilisez: 30s, 5m, 2h"));
                        return;
                    }
                } else {
                    durationMillis = 30 * 60 * 1000; // 30 minutes par défaut
                }

                windScheduler.triggerWind(worldName, durationMillis);
                String formatted = formatDuration(elytraManager.getRemainingTime(worldName));
                sender.sendMessage(mm.deserialize("<green>Vent fort déclenché dans <yellow>" + worldName +
                        "</yellow> pour <yellow>" + formatted + "</yellow>."));
            }
            case "stop" -> {
                if (args.length < 2) {
                    sender.sendMessage(mm.deserialize("<red>Usage: /lcore elytra wind stop <monde>"));
                    return;
                }

                String worldName = args[1];

                if (!windScheduler.isWindActive(worldName)) {
                    sender.sendMessage(mm.deserialize("<yellow>Le vent n'est pas actif dans ce monde."));
                    return;
                }

                windScheduler.stopWind(worldName);
                sender.sendMessage(mm.deserialize("<green>Vent fort arrêté dans <yellow>" + worldName + "</yellow>."));
            }
            default -> sendWindUsage(sender);
        }
    }

    private void sendWindUsage(CommandSender sender) {
        sender.sendMessage(mm.deserialize("<gold>Usage:"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra wind start <monde> [durée]</white> <gray>- Déclenche le vent"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra wind stop <monde></white> <gray>- Arrête le vent"));
        sender.sendMessage(mm.deserialize("<gray>Durées: 30s, 5m, 2h, etc."));
    }

    private void sendElytraUsage(CommandSender sender) {
        sender.sendMessage(mm.deserialize("<gold>Usage:"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra disable <monde> [durée]</white> <gray>- Désactive les elytras"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra enable <monde></white> <gray>- Réactive les elytras"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra status [monde]</white> <gray>- Affiche le statut"));
        sender.sendMessage(mm.deserialize("  <white>/lcore elytra wind start|stop <monde> [durée]</white> <gray>- Gère le vent"));
        sender.sendMessage(mm.deserialize("<gray>Durées: 30s, 5m, 2h, etc."));
    }

    private long parseDuration(String input) {
        if (input == null || input.isEmpty()) return -1;

        try {
            char unit = input.charAt(input.length() - 1);
            long value = Long.parseLong(input.substring(0, input.length() - 1));

            return switch (unit) {
                case 's' -> value * 1000;
                case 'm' -> value * 60 * 1000;
                case 'h' -> value * 60 * 60 * 1000;
                default -> -1;
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String formatDuration(long millis) {
        if (millis == Long.MAX_VALUE) return "indéfini";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            long remainingMinutes = minutes % 60;
            if (remainingMinutes > 0) {
                return hours + "h " + remainingMinutes + "m";
            }
            return hours + "h";
        } else if (minutes > 0) {
            long remainingSeconds = seconds % 60;
            if (remainingSeconds > 0) {
                return minutes + "m " + remainingSeconds + "s";
            }
            return minutes + "m";
        } else {
            return seconds + "s";
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = Arrays.asList("");
        if (args.length == 1)
            suggestions = Arrays.asList("reload", "rl", "conversion", "nick", "unnick", "nametag",
                    "resetvaults", "elytra");
        if (args.length == 2 && (args[0].equalsIgnoreCase("nick") ||
                args[0].equalsIgnoreCase("unnick") ||
                args[0].equalsIgnoreCase("nametag")))
            return null;
        if (args.length == 3 && args[0].equalsIgnoreCase("nametag"))
            suggestions = Arrays.asList("addViewer", "removeViewer", "sendNametag");

        // Elytra tab completion
        if (args[0].equalsIgnoreCase("elytra")) {
            return getElytraTabCompletions(args);
        }

        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length - 1]))
                .collect(Collectors.toList());
    }

    private List<String> getElytraTabCompletions(String[] args) {
        List<String> completions = new ArrayList<>();
        ElytraManager elytraManager = LaBoulangerieCore.PLUGIN.getElytraManager();
        StrongWindScheduler windScheduler = LaBoulangerieCore.PLUGIN.getStrongWindScheduler();

        if (args.length == 2) {
            List<String> subCommands = List.of("disable", "enable", "status", "wind");
            String input = args[1].toLowerCase();
            for (String sub : subCommands) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[1].toLowerCase();
            String input = args[2].toLowerCase();

            if (subCommand.equals("disable") || subCommand.equals("status")) {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getName().toLowerCase().startsWith(input)) {
                        completions.add(world.getName());
                    }
                }
            } else if (subCommand.equals("enable")) {
                for (String worldName : elytraManager.getRestrictedWorlds().keySet()) {
                    if (worldName.toLowerCase().startsWith(input)) {
                        completions.add(worldName);
                    }
                }
            } else if (subCommand.equals("wind")) {
                List<String> windActions = List.of("start", "stop");
                for (String action : windActions) {
                    if (action.startsWith(input)) {
                        completions.add(action);
                    }
                }
            }
        } else if (args.length == 4) {
            String subCommand = args[1].toLowerCase();
            String input = args[3].toLowerCase();

            if (subCommand.equals("disable")) {
                List<String> durations = List.of("30s", "1m", "5m", "10m", "30m", "1h", "2h");
                for (String duration : durations) {
                    if (duration.startsWith(input)) {
                        completions.add(duration);
                    }
                }
            } else if (subCommand.equals("wind")) {
                String windAction = args[2].toLowerCase();
                if (windAction.equals("start")) {
                    for (World world : Bukkit.getWorlds()) {
                        if (world.getName().toLowerCase().startsWith(input)) {
                            completions.add(world.getName());
                        }
                    }
                } else if (windAction.equals("stop")) {
                    for (World world : Bukkit.getWorlds()) {
                        if (world.getName().toLowerCase().startsWith(input) &&
                                windScheduler.isWindActive(world.getName())) {
                            completions.add(world.getName());
                        }
                    }
                }
            }
        } else if (args.length == 5 && args[1].equalsIgnoreCase("wind") && args[2].equalsIgnoreCase("start")) {
            String input = args[4].toLowerCase();
            List<String> durations = List.of("30s", "1m", "5m", "10m", "30m", "1h", "2h");
            for (String duration : durations) {
                if (duration.startsWith(input)) {
                    completions.add(duration);
                }
            }
        }

        return completions;
    }
}
