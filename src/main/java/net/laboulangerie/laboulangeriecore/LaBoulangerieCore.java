package net.laboulangerie.laboulangeriecore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.angeschossen.lands.api.LandsIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriecore.advancements.AdvancementListeners;
import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateListener;
import net.laboulangerie.laboulangeriecore.betonquest.HasHouseCondition;
import net.laboulangerie.laboulangeriecore.betonquest.HousesStockCondition;
import net.laboulangerie.laboulangeriecore.betonquest.KingCondition;
import net.laboulangerie.laboulangeriecore.betonquest.MayorCondition;
import net.laboulangerie.laboulangeriecore.betonquest.RankCondition;
import net.laboulangerie.laboulangeriecore.commands.CoreCommand;
import net.laboulangerie.laboulangeriecore.commands.HatCommand;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.commands.RealNameCommand;
import net.laboulangerie.laboulangeriecore.commands.SeenCmd;
import net.laboulangerie.laboulangeriecore.commands.SpawnCmd;
import net.laboulangerie.laboulangeriecore.commands.SpeedCommand;
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
//import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.HouseShopCmd;
import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.NationHouseHolder;
//import net.laboulangerie.laboulangeriecore.core.houses.nationhouse.NationHousesCmd;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggCommand;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggHeadClick;
import net.laboulangerie.laboulangeriecore.eastereggs.eEggUtil;
import net.laboulangerie.laboulangeriecore.eco.ConversionInv;
import net.laboulangerie.laboulangeriecore.misc.ChestShopListener;
import net.laboulangerie.laboulangeriecore.misc.DisableCraftListener;
import net.laboulangerie.laboulangeriecore.misc.ElytraGenRemover;
import net.laboulangerie.laboulangeriecore.misc.LaBoulangerieExpansion;
import net.laboulangerie.laboulangeriecore.misc.MiscListener;
import net.laboulangerie.laboulangeriecore.misc.TradeOverflowListener;
import net.laboulangerie.laboulangeriecore.misc.TradesHook;
import net.laboulangerie.laboulangeriecore.misc.VaultsReset;
import net.laboulangerie.laboulangeriecore.moreroleplay.RollCommands;
import net.laboulangerie.laboulangeriecore.speedpaths.SpeedPathListener;
import net.laboulangerie.laboulangeriecore.speedpaths.SpeedPathManager;
import net.laboulangerie.laboulangeriecore.tab.TabListener;
import net.milkbowl.vault.economy.Economy;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    public static Economy econ = null;
    public static HousesManager housesManager;
    public static NationHouseHolder nationHouseHolder;
    public static LandsIntegration apiLands;

    private ComponentRenderer componentRenderer;
    private SpeedPathManager speedPathManager;
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

        speedPathManager = new SpeedPathManager();
        speedPathManager.load();

        try {
            eEggUtil.ensureFilesExist();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EventsManager.innit();

        VaultsReset.reset();

        registerListeners();

        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        getCommand("faveursdivines").setExecutor(new DivineFavorsCmd());
        getCommand("housewand").setExecutor(new HouseWandCmd());
        getCommand("createhouse").setExecutor(new CreateHouseCmd());
        getCommand("listhouse").setExecutor(new ListHouseCmd());
        getCommand("deletehouse").setExecutor(new DeleteHouseCmd());
        getCommand("houseflag").setExecutor(new HouseFlagCmd());
        getCommand("housemembers").setExecutor(new HouseMembersCmd());
        //getCommand("nationhouses").setExecutor(new NationHousesCmd());
        getCommand("core").setExecutor(new CoreCommand());
        getCommand("easteregg").setExecutor(new eEggCommand());
        //getCommand("houseshop").setExecutor(new HouseShopCmd());
        getCommand("spawn").setExecutor(new SpawnCmd());
        getCommand("seen").setExecutor(new SeenCmd());
        getCommand("event").setExecutor(new EventCmd());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("realname").setExecutor(new RealNameCommand());
        getCommand("hat").setExecutor(new HatCommand());
        getCommand("roll").setExecutor(new RollCommands());
        getCommand("wroll").setExecutor(new RollCommands());
        // Link or simple message commands
        getCommand("wiki").setExecutor(new LinkCommands());
        getCommand("youtube").setExecutor(new LinkCommands());
        getCommand("twitter").setExecutor(new LinkCommands());
        getCommand("github").setExecutor(new LinkCommands());
        getCommand("librahost").setExecutor(new LinkCommands());

        if (getServer().getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("towny_is_king",
                    new PlayerConditionFactory() {
                        @Override
                        public PlayerCondition parsePlayer(Instruction instruction) throws InstructionParseException {
                            return new KingCondition();
                        }
                    }, null);
            BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("towny_is_mayor",
                    new PlayerConditionFactory() {
                        @Override
                        public PlayerCondition parsePlayer(Instruction instruction) throws InstructionParseException {
                            return new MayorCondition();
                        }
                    }, null);
            BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("nation_houses_has_stocks",
                    new PlayerConditionFactory() {
                        @Override
                        public PlayerCondition parsePlayer(Instruction instruction) throws InstructionParseException {
                            return new HousesStockCondition();
                        }
                    }, null);
            BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("towny_has_house",
                    new PlayerConditionFactory() {
                        @Override
                        public PlayerCondition parsePlayer(Instruction instruction) throws InstructionParseException {
                            return new HasHouseCondition();
                        }
                    }, null);
            BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("towny_has_rank",
                    new PlayerConditionFactory() {
                        @Override
                        public PlayerCondition parsePlayer(Instruction instruction) throws InstructionParseException {
                            return new RankCondition(instruction.getPart(1), instruction.getPart(2));
                        }
                    }, null);
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

        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().stream().forEach(p -> {
                    p.getInventory().forEach(item -> {
                        if (item != null
                                && (item.getType() == Material.EMERALD || item.getType() == Material.DIAMOND
                                        || item.getType() == Material.AMETHYST_SHARD)
                                && item.getItemMeta().hasCustomModelData()
                                && item.getItemMeta().getCustomModelData() == 1) {
                            p.damage(1);
                        }
                    });
                });
            }
        }.runTaskTimer(this, 100, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                long freeSpace = new File("/").getFreeSpace() / 1024 / 1024 / 1024; // in Go
                if (freeSpace < 1) {
                    getServer().getOnlinePlayers().stream().forEach(p -> {
                        p.kick(Component.text("Arrêt d'urgence, contactez les administrateurs :)"));
                    });
                    getLogger().severe("Almost no disk space left ! Shutting down in prevention !");
                    getServer().shutdown();
                }
            }
        }.runTaskTimer(this, 0, 20 * 60 * 60);

        apiLands = LandsIntegration.of(PLUGIN);

        getLogger().info("Enabled Successfully");
    }

    public ComponentRenderer getComponentRenderer() {
        return componentRenderer;
    }

    public SpeedPathManager getSpeedPathManager() {
        return speedPathManager;
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
                new TabListener(), new ElytraGenRemover(), new SpeedPathListener(),
                new TradesHook(), new HouseShop(), new HouseWandListener(), new HouseListener(), new eEggHeadClick(),
                new ConversionInv(), miscListener, new AdvancementListeners(), new DragonsListener(),
                new TradeOverflowListener(), new AuthenticateListener(), new DisableCraftListener());

        if (getServer().getPluginManager().getPlugin("QuickShop-Hikari") != null)
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
