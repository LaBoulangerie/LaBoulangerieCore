package net.laboulangerie.laboulangeriecore;

import java.io.IOException;
import java.util.Arrays;

import net.laboulangerie.laboulangeriecore.eastereggs.Utils.eEggFileUtil;
import net.laboulangerie.laboulangeriecore.eastereggs.command.eEggCommand;
import net.laboulangerie.laboulangeriecore.eastereggs.event.eEggHeadClick;
import net.laboulangerie.laboulangeriecore.nametag.NameTagListener;
import net.laboulangerie.laboulangeriecore.nametag.NameTagManager;
import net.laboulangerie.laboulangeriecore.nametag.ReloadNameTagCmd;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.commands.chestshop.ChestShopListener;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.misc.FirstJoinActions;
import net.laboulangerie.laboulangeriecore.points.DivinePointsCmd;
import net.laboulangerie.laboulangeriecore.tab.TabListener;
import net.laboulangerie.laboulangeriecore.villagers.TradesHook;
import net.milkbowl.vault.economy.Economy;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    public static Economy econ = null;

    private ComponentRenderer componentRenderer;

    private NameTagManager nameTagManager;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LaBoulangerieCore.PLUGIN = this;

        componentRenderer = new ComponentRenderer();
        nameTagManager = new NameTagManager();

        saveDefaultConfig();
        registerListeners();

        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        getCommand("pointsdivins").setExecutor(new DivinePointsCmd());
        getCommand("reloadnametag").setExecutor(new ReloadNameTagCmd());
        // Link or simple message commands
        getCommand("wiki").setExecutor(new LinkCommands());
        getCommand("discord").setExecutor(new LinkCommands());
        getCommand("youtube").setExecutor(new LinkCommands());
        getCommand("twitter").setExecutor(new LinkCommands());
        getCommand("map").setExecutor(new LinkCommands());
        getCommand("github").setExecutor(new LinkCommands());
        
        /** EasterEggs */
        try {
            eEggFileUtil.ensureFilesExist();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(new eEggHeadClick(), this);
        getCommand("easteregg").setExecutor(new eEggCommand());

        getLogger().info("Enabled Successfully");
    }

    public ComponentRenderer getComponentRenderer() {
        return componentRenderer;
    }

    public NameTagManager getNameTagManager() {
        return nameTagManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded");
    }

    private void registerListeners() {
        Arrays.asList(
                new LoreUpdater(), new TabListener(), new NameTagListener(), new ElytraGenRemover(),
                new ChestShopListener(),
                new TradesHook(), new FirstJoinActions())
                .forEach(l -> getServer().getPluginManager().registerEvents(l, this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
