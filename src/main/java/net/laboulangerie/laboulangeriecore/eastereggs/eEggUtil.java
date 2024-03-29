package net.laboulangerie.laboulangeriecore.eastereggs;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class eEggUtil {
    public static YamlConfiguration eggsData;
    public static File eggsFile = new File(LaBoulangerieCore.PLUGIN.getDataFolder(), "/eggs.yml");

    /** Here I get the amount of eastereggs */
    public static Integer getMaxAmount() {
        List<String> list = eEggUtil.eggsData.getStringList("eggs");
        return list.size();
    }

    /** Here I get how much eastereggs the player have find */
    public static Integer getPlayerAmount(Player p) {
        Optional<YamlConfiguration> data = UsersData.get(p);
        if (data.isPresent()) {
            return data.get().getStringList("eggs").size();
        }
        return 0;
    }

    /** Here I get all the gifts and I get a random integer for give a random gift to the player */
    public static void giveGift(Player p) {
        List<String> keys = new ArrayList<>();

        for (String key : LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("eastereggs.settings.gifts")
                .getKeys(false)) {
            keys.add(key);
        }

        if (keys.size() == 0) return;

        Random rdm = new Random();
        int i = rdm.nextInt(keys.size());

        if (LaBoulangerieCore.PLUGIN.getConfig()
                .getBoolean("eastereggs.settings.gifts." + keys.get(i) + ".command") == true) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LaBoulangerieCore.PLUGIN.getConfig()
                    .getString("eastereggs.settings.gifts." + keys.get(i) + ".gift").replace("%player%", p.getName()));
        } else {
            String[] item = LaBoulangerieCore.PLUGIN.getConfig()
                    .getString("eastereggs.settings.gifts." + keys.get(i) + ".gift").split(":");
            ItemStack it = new ItemStack(Material.getMaterial(item[0]), Integer.parseInt(item[1]));
            p.getInventory().addItem(it);
        }
    }

    /** Here I prepare the message "validated" */
    public static void sendValidation(Player p) {
        List<String> titles = LaBoulangerieCore.PLUGIN.getConfig().getStringList("eastereggs.messages.validated-title")
                .stream().map(str -> str.replace("%max-amount%", getMaxAmount().toString()).replace("%amount%",
                        getPlayerAmount(p).toString()))
                .collect(Collectors.toList());

        p.showTitle(Title.title(Component.text(titles.get(0)), Component.text(titles.get(1)),
                Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofMillis(500))));
        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1.0f);
    }

    /** Here I prepare the message "already validated" */
    public static void sendAlreadyValidated(Player p) {
        p.sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.already-validated"));
    }

    public static String getBlockIdentifier(Block block) {
        return block.getWorld().getName() + "!" + block.getX() + "!" + block.getY() + "!" + block.getZ();
    }

    public static void ensureFilesExist() throws IOException {
        if (!eggsFile.exists()) eggsFile.createNewFile();
        eggsData = YamlConfiguration.loadConfiguration(eggsFile);
        if (!eggsData.isSet("eggs")) eggsData.set("eggs", new ArrayList<String>());
    }
}
