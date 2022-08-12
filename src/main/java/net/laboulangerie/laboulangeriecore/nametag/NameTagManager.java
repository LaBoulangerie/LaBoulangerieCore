package net.laboulangerie.laboulangeriecore.nametag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NameTagManager {
    public static List<String> rawNameTags;

    private BukkitTask textUpdateTask;

    public NameTagManager() {}

    public void enable() {
        ConfigurationSection configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("nametag");
        rawNameTags = new ArrayList<>();
        for (String key : configTabSection.getKeys(false)) rawNameTags.add(configTabSection.getString(key));

        textUpdateTask = new BukkitRunnable() {

            @Override
            public void run() {
                PlayerNameTag.nameTags.forEach(PlayerNameTag::updateText);
            }
            
        }.runTaskTimer(LaBoulangerieCore.PLUGIN, 20, 20);
    }

    public void disable() {
        textUpdateTask.cancel();
        PlayerNameTag.nameTags.forEach(PlayerNameTag::destroy);
    }
}
