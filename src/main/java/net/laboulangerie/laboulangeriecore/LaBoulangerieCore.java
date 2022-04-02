package net.laboulangerie.laboulangeriecore;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.tab.TabListener;
import net.laboulangerie.laboulangeriecore.villagers.TradesHook;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;

    private ComponentRenderer componentRenderer;

    @Override
    public void onEnable() {
        LaBoulangerieCore.PLUGIN = this;

        this.componentRenderer = new ComponentRenderer();

        this.saveDefaultConfig();
        this.registerListeners();

        this.getCommand("authenticate").setExecutor(new AuthenticateCommand());
        // Link or simple message commands
        this.getCommand("wiki").setExecutor(new LinkCommands());
        this.getCommand("discord").setExecutor(new LinkCommands());
        this.getCommand("youtube").setExecutor(new LinkCommands());
        this.getCommand("twitter").setExecutor(new LinkCommands());
        this.getCommand("map").setExecutor(new LinkCommands());
        this.getCommand("github").setExecutor(new LinkCommands());

        getLogger().info("Enabled Successfully");
    }

    public ComponentRenderer getComponentRenderer() {
        return componentRenderer;
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }

    private void registerListeners() {
        Arrays.asList(
                new LoreUpdater(), new TabListener(), new ElytraGenRemover(),
                new TradesHook()).forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }
}
