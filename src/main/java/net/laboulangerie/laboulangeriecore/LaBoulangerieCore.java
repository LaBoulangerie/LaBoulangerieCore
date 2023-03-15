package net.laboulangerie.laboulangeriecore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.advancements.AdvancementListeners;
import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.LoreUpdater;
import net.laboulangerie.laboulangeriecore.betonquest.HasHouseCondition;
import net.laboulangerie.laboulangeriecore.betonquest.HousesStockCondition;
import net.laboulangerie.laboulangeriecore.betonquest.KingCondition;
import net.laboulangerie.laboulangeriecore.betonquest.MayorCondition;
import net.laboulangerie.laboulangeriecore.betonquest.RankCondition;
import net.laboulangerie.laboulangeriecore.commands.CoreCommand;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.commands.SeenCmd;
import net.laboulangerie.laboulangeriecore.commands.SpawnCmd;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.core.UsersData;
import net.laboulangerie.laboulangeriecore.core.end.DragonsListener;
import net.laboulangerie.laboulangeriecore.core.event.EventCmd;
import net.laboulangerie.laboulangeriecore.core.event.EventsManager;
import net.laboulangerie.laboulangeriecore.core.favors.DivineFavorsCmd;
import net.laboulangerie.laboulangeriecore.core.houses.CreateHouseCmd;
import net.laboulangerie.laboulangeriecore.core.houses.DeleteHouseCmd;
import net.laboulangerie.laboulangeriecore.core.houses.House;
import net.laboulangerie.laboulangeriecore.core.houses.HouseFlagCmd;
import net.laboulangerie.laboulangeriecore.core.houses.HouseListener;
import net.laboulangerie.laboulangeriecore.core.houses.HouseMembersCmd;
import net.laboulangerie.laboulangeriecore.core.houses.HousesManager;
import net.laboulangerie.laboulangeriecore.core.houses.ListHouseCmd;
import net.laboulangerie.laboulangeriecore.core.houses.housewand.HouseWandCmd;
import net.laboulangerie.laboulangeriecore.core.houses.housewand.HouseWandListener;
import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.HouseShop;
import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.HouseShopCmd;
import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.NationHouseHolder;
import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.NationHousesCmd;
import net.laboulangerie.laboulangeriecore.core.nametag.NameTagListener;
import net.laboulangerie.laboulangeriecore.core.nametag.NameTagManager;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggCommand;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggHeadClick;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggUtil;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;
import net.laboulangerie.laboulangeriecore.misc.ChestShopListener;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.misc.LaBoulangerieExpansion;
import net.laboulangerie.laboulangeriecore.misc.MiscListener;
import net.laboulangerie.laboulangeriecore.misc.TradesHook;
import net.laboulangerie.laboulangeriecore.tab.TabListener;
import net.milkbowl.vault.economy.Economy;
import pl.betoncraft.betonquest.BetonQuest;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    public static Economy econ = null;
    public static HousesManager housesManager;
    public static NationHouseHolder nationHouseHolder;

    private ComponentRenderer componentRenderer;
    private NameTagManager nameTagManager;
    private MiscListener miscListener = new MiscListener();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LaBoulangerieCore.PLUGIN = this;
        ConfigurationSerialization.registerClass(House.class);
        UsersData.init();
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
        nameTagManager.enable();

        try {
            eEggUtil.ensureFilesExist();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EventsManager.innit();

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
        getCommand("core").setExecutor(new CoreCommand());
        getCommand("easteregg").setExecutor(new eEggCommand());
        getCommand("houseshop").setExecutor(new HouseShopCmd());
        getCommand("spawn").setExecutor(new SpawnCmd());
        getCommand("seen").setExecutor(new SeenCmd());
        getCommand("event").setExecutor(new EventCmd());
        // Link or simple message commands
        getCommand("wiki").setExecutor(new LinkCommands());
        getCommand("discord").setExecutor(new LinkCommands());
        getCommand("youtube").setExecutor(new LinkCommands());
        getCommand("twitter").setExecutor(new LinkCommands());
        getCommand("map").setExecutor(new LinkCommands());
        getCommand("github").setExecutor(new LinkCommands());
        getCommand("librahost").setExecutor(new LinkCommands());

        if (getServer().getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuest.getInstance().registerConditions("towny_is_king", KingCondition.class);
            BetonQuest.getInstance().registerConditions("towny_is_mayor", MayorCondition.class);
            BetonQuest.getInstance().registerConditions("nation_houses_has_stocks", HousesStockCondition.class);
            BetonQuest.getInstance().registerConditions("towny_has_house", HasHouseCondition.class);
            BetonQuest.getInstance().registerConditions("towny_has_rank", RankCondition.class);
            getLogger().info("Hooked in BetonQuest!");
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LaBoulangerieExpansion().register();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> messages = getConfig().getStringList("auto-messages");
                if (messages.size() == 0)
                    return;
                Random rand = new Random();
                getServer()
                        .broadcast(MiniMessage.miniMessage().deserialize(messages.get(rand.nextInt(messages.size()))));
            }
        }.runTaskTimerAsynchronously(this, 200, getConfig().getInt("auto-messages-interval") * 20);

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
        nameTagManager.disable();
        getLogger().info("Disabled");
    }

    private void registerListeners() {
        List<Listener> listeners = Arrays.asList(
                new LoreUpdater(), new TabListener(), new NameTagListener(), new ElytraGenRemover(),
                new TradesHook(), new HouseShop(),
                new HouseWandListener(), new HouseListener(), new eEggHeadClick(),
                new ConversionInv(), miscListener, new AdvancementListeners(), new DragonsListener()
        );
        if (getServer().getPluginManager().getPlugin("QuickShop") != null)
            getServer().getPluginManager().registerEvents(new ChestShopListener(), this);

        listeners.forEach(l -> getServer().getPluginManager().registerEvents(l, this));
        miscListener.registerProtocolLibListeners();
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