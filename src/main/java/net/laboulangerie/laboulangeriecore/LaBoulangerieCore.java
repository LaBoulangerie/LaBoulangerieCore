package net.laboulangerie.laboulangeriecore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.commands.chestshop.ChestShopListener;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.eastereggs.Utils.eEggFileUtil;
import net.laboulangerie.laboulangeriecore.eastereggs.command.eEggCommand;
import net.laboulangerie.laboulangeriecore.eastereggs.event.eEggHeadClick;
import net.laboulangerie.laboulangeriecore.favors.DivineFavorsCmd;
import net.laboulangerie.laboulangeriecore.houses.CreateHouseCmd;
import net.laboulangerie.laboulangeriecore.houses.DeleteHouseCmd;
import net.laboulangerie.laboulangeriecore.houses.House;
import net.laboulangerie.laboulangeriecore.houses.HouseFlagCmd;
import net.laboulangerie.laboulangeriecore.houses.HouseListener;
import net.laboulangerie.laboulangeriecore.houses.HouseMembersCmd;
import net.laboulangerie.laboulangeriecore.houses.HousesManager;
import net.laboulangerie.laboulangeriecore.houses.ListHouseCmd;
import net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandCmd;
import net.laboulangerie.laboulangeriecore.houses.housewand.HouseWandListener;
import net.laboulangerie.laboulangeriecore.houses.nationhouse.HouseShop;
import net.laboulangerie.laboulangeriecore.houses.nationhouse.HouseShopCmd;
import net.laboulangerie.laboulangeriecore.houses.nationhouse.NationHouseHolder;
import net.laboulangerie.laboulangeriecore.houses.nationhouse.NationHousesCmd;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.misc.FirstJoinActions;
import net.laboulangerie.laboulangeriecore.misc.HasHouseCondition;
import net.laboulangerie.laboulangeriecore.misc.HousesStockCondition;
import net.laboulangerie.laboulangeriecore.misc.KingCondition;
import net.laboulangerie.laboulangeriecore.nametag.NameTagListener;
import net.laboulangerie.laboulangeriecore.nametag.NameTagManager;
import net.laboulangerie.laboulangeriecore.nametag.ReloadNameTagCmd;
import net.laboulangerie.laboulangeriecore.tab.TabListener;
import net.laboulangerie.laboulangeriecore.villagers.TradesHook;
import net.milkbowl.vault.economy.Economy;
import pl.betoncraft.betonquest.BetonQuest;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    public static Economy econ = null;
    public static HousesManager housesManager;
    public static NationHouseHolder nationHouseHolder;

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
        ConfigurationSerialization.registerClass(House.class);
        housesManager = new HousesManager(new File(getDataFolder(), "houses"));

        try {
            housesManager.loadHouses();
        } catch (ClassNotFoundException | IOException e) {
            getLogger().severe("Failed to load houses, disabling plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        nationHouseHolder = new NationHouseHolder();

        try {
            nationHouseHolder.loadData();
        } catch (IOException e) {
            getLogger().severe("Failed to load nation houses, disabling plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        componentRenderer = new ComponentRenderer();
        nameTagManager = new NameTagManager();

        saveDefaultConfig();
        registerListeners();

        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        getCommand("faveursdivines").setExecutor(new DivineFavorsCmd());
        getCommand("housewand").setExecutor(new HouseWandCmd());
        getCommand("createhouse").setExecutor(new CreateHouseCmd());
        getCommand("listhouse").setExecutor(new ListHouseCmd());
        getCommand("deletehouse").setExecutor(new DeleteHouseCmd());
        getCommand("houseflag").setExecutor(new HouseFlagCmd());
        getCommand("housemembers").setExecutor(new HouseMembersCmd());
        getCommand("nationhouses").setExecutor(new NationHousesCmd());
        getCommand("reloadnametag").setExecutor(new ReloadNameTagCmd());
        getCommand("houseshop").setExecutor(new HouseShopCmd());
        // Link or simple message commands
        getCommand("wiki").setExecutor(new LinkCommands());
        getCommand("discord").setExecutor(new LinkCommands());
        getCommand("youtube").setExecutor(new LinkCommands());
        getCommand("twitter").setExecutor(new LinkCommands());
        getCommand("map").setExecutor(new LinkCommands());
        getCommand("github").setExecutor(new LinkCommands());

        if (getServer().getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuest.getInstance().registerConditions("towny_is_king", KingCondition.class);
            BetonQuest.getInstance().registerConditions("nation_houses_has_stocks", HousesStockCondition.class);
            BetonQuest.getInstance().registerConditions("towny_has_house", HasHouseCondition.class);
            getLogger().info("Hooked in BetonQuest!");
        }

        getLogger().info("Enabled Successfully");

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
        try {
            housesManager.saveHouses();
        } catch (IOException e) {
            getLogger().severe("Failed to save houses while disabling");
            e.printStackTrace();
        }
        try {
            nationHouseHolder.saveData();
        } catch (IOException e) {
            getLogger().severe("Failed to save nation houses while disabling");
            e.printStackTrace();
        }
        getLogger().info("Disabled");
    }

    private void registerListeners() {
        List<Listener> listeners = Arrays.asList(
                new LoreUpdater(), new TabListener(), new NameTagListener(), new ElytraGenRemover(),
                new TradesHook(), new HouseShop(),
                new FirstJoinActions(), new HouseWandListener(),
                new HouseListener()
        );
        if (getServer().getPluginManager().getPlugin("ChestShop") != null) listeners.add(new ChestShopListener());

        listeners.forEach(l -> getServer().getPluginManager().registerEvents(l, this));
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
