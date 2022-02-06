package net.laboulangerie.laboulangeriecore;

import org.bukkit.plugin.java.JavaPlugin;

public class LaBoulangerieCore extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }
}
