package net.laboulangerie.laboulangeriecore.core.event;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class EventCmd implements TabExecutor {
    // TODO check sender, error messages
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 2) return false;
        if (!EventsManager.hasEvent(args[0])) return false;

        EventState event = EventsManager.getEvent(args[0]);
        switch (args[1]) {
            case "start":
                event.start();
                break;
            case "stop":
                event.stop();
                break;
            case "reset":
                event.reset();
                break;
            case "nextStep":
                event.nextStep((Player) sender);
                break;
            default:
                break;
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = null;
        if (args.length == 2) suggestions = Arrays.asList("start", "stop", "nextStep", "reset");
        return suggestions == null ? null : suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }
}
