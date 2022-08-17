package net.laboulangerie.laboulangeriecore.nametag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NameTagManager {
    public static List<String> rawNameTags;

    private BukkitTask textUpdateTask;

    public void enable() {
        ConfigurationSection configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("nametag");
        rawNameTags = new ArrayList<>();
        for (String key : configTabSection.getKeys(false)) rawNameTags.add(configTabSection.getString(key));

        textUpdateTask = new BukkitRunnable() {
            @Override
            public void run() { PlayerNameTag.nameTags.forEach(PlayerNameTag::updateText); }  
        }.runTaskTimer(LaBoulangerieCore.PLUGIN, 20, 20);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
            LaBoulangerieCore.PLUGIN, ListenerPriority.MONITOR, PacketType.Play.Server.NAMED_ENTITY_SPAWN
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player newPlayer = Bukkit.getPlayer(event.getPacket().getUUIDs().getValues().get(0));
                if (newPlayer.getUniqueId().version() == 2) return; // UUIDv2 is used for NPCs

                PlayerNameTag.get(event.getPlayer()).addViewer(newPlayer);
            }
        });
    }

    public void disable() {
        textUpdateTask.cancel();
        PlayerNameTag.nameTags.forEach(PlayerNameTag::destroy);
        PlayerNameTag.nameTags.clear();
    }
}
