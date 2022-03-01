package net.laboulangerie.laboulangeriecore;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.commands.Discord;
import net.laboulangerie.laboulangeriecore.commands.Github;
import net.laboulangerie.laboulangeriecore.commands.Map;
import net.laboulangerie.laboulangeriecore.commands.Twitter;
import net.laboulangerie.laboulangeriecore.commands.Wiki;
import net.laboulangerie.laboulangeriecore.commands.Youtube;
import net.laboulangerie.laboulangeriecore.tab.TabListener;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;

    @Override
    public void onEnable() {
        LaBoulangerieCore.PLUGIN = this;

        this.saveDefaultConfig();
        this.registerListeners();

        this.getCommand("authenticate").setExecutor(new AuthenticateCommand());
        this.getCommand("wiki").setExecutor(new Wiki(this));
        this.getCommand("discord").setExecutor(new Discord(this));
        this.getCommand("youtube").setExecutor(new Youtube(this));
        this.getCommand("twitter").setExecutor(new Twitter(this));
        this.getCommand("map").setExecutor(new Map(this));
        this.getCommand("github").setExecutor(new Github(this));

        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }

    private void registerListeners() {
        Arrays.asList(
                new LoreUpdater(), new TabListener())
                .forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }
}
