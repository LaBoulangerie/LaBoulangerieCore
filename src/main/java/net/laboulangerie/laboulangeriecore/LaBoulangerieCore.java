package net.laboulangerie.laboulangeriecore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.laboulangerie.laboulangeriecore.advancements.AdvancementListeners;
import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateCommand;
import net.laboulangerie.laboulangeriecore.authenticate.AuthenticateListener;
import net.laboulangerie.laboulangeriecore.commands.CoreCommand;
import net.laboulangerie.laboulangeriecore.commands.HatCommand;
import net.laboulangerie.laboulangeriecore.commands.LinkCommands;
import net.laboulangerie.laboulangeriecore.commands.RealNameCommand;
import net.laboulangerie.laboulangeriecore.commands.SeenCmd;
import net.laboulangerie.laboulangeriecore.commands.SpeedCommand;
import net.laboulangerie.laboulangeriecore.core.ComponentRenderer;
import net.laboulangerie.laboulangeriecore.core.UsersData;
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
import net.laboulangerie.laboulangeriecore.moreroleplay.ConsequenceCommand;
import net.laboulangerie.laboulangeriecore.moreroleplay.MeCommand;
import net.laboulangerie.laboulangeriecore.moreroleplay.NarrationCommand;
import net.laboulangerie.laboulangeriecore.moreroleplay.RollCommand;
import net.laboulangerie.laboulangeriecore.moreroleplay.SpyRPCommands;
import net.laboulangerie.laboulangeriecore.moreroleplay.WNarrationCommand;
import net.laboulangerie.laboulangeriecore.moreroleplay.WrollCommand;
import net.laboulangerie.laboulangeriecore.speedpaths.SpeedPathListener;
import net.laboulangerie.laboulangeriecore.speedpaths.SpeedPathManager;
import net.laboulangerie.laboulangeriecore.tab.TabListener;

import net.milkbowl.vault.economy.Economy;

public class LaBoulangerieCore extends JavaPlugin {
    public static LaBoulangerieCore PLUGIN;
    public static Economy econ = null;

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
        UsersData.init();

        componentRenderer = new ComponentRenderer();

        speedPathManager = new SpeedPathManager();
        speedPathManager.load();

        try {
            eEggUtil.ensureFilesExist();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VaultsReset.reset();

        registerListeners();

        getCommand("authenticate").setExecutor(new AuthenticateCommand());
        getCommand("core").setExecutor(new CoreCommand());
        getCommand("easteregg").setExecutor(new eEggCommand());
        getCommand("seen").setExecutor(new SeenCmd());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("realname").setExecutor(new RealNameCommand());
        getCommand("hat").setExecutor(new HatCommand());
        getCommand("roll").setExecutor(new RollCommand());
        getCommand("wroll").setExecutor(new WrollCommand());
        getCommand("spyrp").setExecutor(new SpyRPCommands());
        getCommand("me").setExecutor(new MeCommand());
        getCommand("narration").setExecutor(new NarrationCommand());
        getCommand("wnarration").setExecutor(new WNarrationCommand());
        getCommand("consequence").setExecutor(new ConsequenceCommand());
        // Link or simple message commands
        getCommand("wiki").setExecutor(new LinkCommands());
        getCommand("youtube").setExecutor(new LinkCommands());
        getCommand("twitter").setExecutor(new LinkCommands());
        getCommand("github").setExecutor(new LinkCommands());
        getCommand("librahost").setExecutor(new LinkCommands());

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

        getLogger().info("Enabled Successfully");
    }

    @Override
    public void onLoad() {
        LaBoulangerieCore.PLUGIN = this;
    }

    public ComponentRenderer getComponentRenderer() {
        return componentRenderer;
    }

    public SpeedPathManager getSpeedPathManager() {
        return speedPathManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }

    private void registerListeners() {
        List<Listener> listeners = Arrays.asList(
                new TabListener(), new ElytraGenRemover(), new SpeedPathListener(),
                new TradesHook(), new eEggHeadClick(),
                new ConversionInv(), miscListener, new AdvancementListeners(),
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
