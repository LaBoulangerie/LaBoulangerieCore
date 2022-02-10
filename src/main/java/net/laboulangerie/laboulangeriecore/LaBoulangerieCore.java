package net.laboulangerie.laboulangeriecore;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;

public class LaBoulangerieCore extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Enabled Successfully");
        getCommand("authenticate").setExecutor(new AuthenticateCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }
}
