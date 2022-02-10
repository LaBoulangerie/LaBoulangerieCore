package net.laboulangerie.laboulangeriecore;

import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;

public class LaBoulangerieCore extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }
}
