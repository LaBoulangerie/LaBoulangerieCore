package net.laboulangerie.laboulangeriecore;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    @Override
    public void onEnable() {
        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        LaBoulangerieCore.PLUGIN = this;
        getServer().getPluginManager().registerEvents(new LoreUpdater(), this);

        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }
}
