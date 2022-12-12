package net.laboulangerie.laboulangeriecore.core.end;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class EggManager {
    private static Map<Location, EggBreakingProgress> eggs = new HashMap<>();

    public static void click(Player player, Location loc) {
        EggBreakingProgress progress = eggs.getOrDefault(loc, new EggBreakingProgress());
        if (progress.registerClick()) {
            loc.getBlock().breakNaturally();
            eggs.remove(loc);
            return;
        }

        eggs.putIfAbsent(loc, progress);
        sendActionBar(player, progress);
    }

    public static void eggTeleported(Location loc) { eggs.remove(loc); }

    private static void sendActionBar(Player player, EggBreakingProgress egg) {
        String[] progressBar = {"a","c","c","c","c","c","c","c","e"};
        int charactersToFill = (int) Math.round(progressBar.length*egg.getProgression());
        for (int i = 0; i < charactersToFill; i++) {
            switch (progressBar[i]) {
                case "a":
                    progressBar[i] = "b";
                    break;
                case "e":
                    progressBar[i] = "f";
                    break;
                default:
                    progressBar[i] = "d";
                    break;
            }
        }

        player.sendActionBar(Component.text(List.of(progressBar).stream().reduce("", (a, b) -> a + " " + b)).font(Key.key("bread_dough", "icons")));
    }
}
