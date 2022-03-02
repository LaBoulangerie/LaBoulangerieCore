package net.laboulangerie.laboulangeriecore;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.tab.TabListener;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;

    @Override
    public void onEnable() {
        LaBoulangerieCore.PLUGIN = this;

        this.saveDefaultConfig();
        this.registerListeners();

        this.getCommand("authenticate").setExecutor(new AuthenticateCommand());

        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }

    private void registerListeners() {
        Arrays.asList(
                new LoreUpdater(), new TabListener(), new ElytraGenRemover())
                .forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }
}
