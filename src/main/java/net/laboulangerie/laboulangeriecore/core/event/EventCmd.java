package net.laboulangerie.laboulangeriecore.core.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class EventCmd implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§bList of registered events:");
            List<String> names = EventsManager.getEvents();
            for (String name : names) {
                EventState event = EventsManager.getEvent(name);
                String status = event.hasStarted() ? "§astarted" : event.hasEnded() ? "§4ended" : "§7off";
                sender.sendMessage("§l├§8" + name + " §6- " + status);
            }
            return true;
        }
        if (args.length < 2) return false;
        if (!EventsManager.hasEvent(args[0])) {
            sender.sendMessage("§4No event named: "+args[0]);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You need to be in-game to use this command.");
            return true;
        }

        EventState event = EventsManager.getEvent(args[0]);
        switch (args[1].toLowerCase()) {
            case "start":
                event.start();
                sender.sendMessage("§aEvent started!");
                break;
            case "stop":
                event.stop();
                sender.sendMessage("§aEvent stopped!");
                break;
            case "reset":
                event.reset();
                sender.sendMessage("§aEvent reset!");
                break;
            case "nextstep":
                event.nextStep((Player) sender);
                sender.sendMessage("§aExecuted next step!");
                break;
            case "status":
                sender.sendMessage(
                    "§bThe event §e"
                    + event.getName()
                    + " §b is currently "
                    + (event.hasStarted()
                        ? "§arunning §7(" + event.getStage() + "/" + event.getTotalStages() +")"
                        : event.hasEnded() ? "§4ended" : "§7off"
                    )
                );
                for (int i = 0; i < event.getSteps().size(); i++) {
                    sender.sendMessage((i <= event.getStage()-1 ? "§a├ " : "§7├ ") + event.getSteps().get(i).getName());
                }
                break;
            case "goto":
                if (args.length < 3) return false;
                if (event.goTo(args[2].replaceAll("_", " "))) {
                    sender.sendMessage("§aJumped to step : " + args[2].replaceAll("_", " "));
                }else {
                    sender.sendMessage("§cCouldn't jump to step: " + args[2].replaceAll("_", " ") + ", either it doesn't exist or the event isn't running.");
                }
                break;
            case "run":
                if (args.length < 3) return false;
                if (event.run((Player) sender, args[2].replaceAll("_", " "))) {
                    sender.sendMessage("§aRan step : " + args[2].replaceAll("_", " "));
                }else {
                    sender.sendMessage("§cCouldn't run step: " + args[2].replaceAll("_", " ") + ", either it doesn't exist or the event isn't running.");
                }
                break;
            default:
                return false;
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = null;
        if (args.length == 2) suggestions = Arrays.asList("start", "stop", "nextStep", "reset", "goto", "run", "status");
        if (args.length == 1) suggestions = EventsManager.getEvents();
        if (args.length == 3 && (args[1].equalsIgnoreCase("goto") || args[1].equalsIgnoreCase("run"))) {
            EventState state = EventsManager.getEvent(args[0]);
            if (state != null)
                suggestions = state.getSteps().stream().map(step -> step.getName().replaceAll(" ", "_")).collect(Collectors.toList());
        }
        return suggestions == null ? new ArrayList<>() : suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }
}
