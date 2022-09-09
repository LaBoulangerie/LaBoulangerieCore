package net.laboulangerie.laboulangeriecore.core.nametag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /**
     * A cache linking entity id to its corresponding {@link Player}
     */
    public static Map<Integer, Player> idToPlayer;

    private BukkitTask textUpdateTask;

    public void enable() {
        ConfigurationSection configTabSection = LaBoulangerieCore.PLUGIN.getConfig().getConfigurationSection("nametag");
        rawNameTags = new ArrayList<>();
        idToPlayer = new HashMap<>();

        for (String key : configTabSection.getKeys(false)) rawNameTags.add(configTabSection.getString(key));

        Bukkit.getOnlinePlayers().forEach(p -> {
            idToPlayer.put(p.getEntityId(), p);
            new PlayerNameTag(p);
        });

        textUpdateTask = new BukkitRunnable() {
            @Override
            public void run() { PlayerNameTag.nameTags.forEach(PlayerNameTag::updateText); }  
        }.runTaskTimer(LaBoulangerieCore.PLUGIN, 20, 20);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
            LaBoulangerieCore.PLUGIN, ListenerPriority.MONITOR, PacketType.Play.Server.NAMED_ENTITY_SPAWN
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player newPlayer = idToPlayer.get(event.getPacket().getIntegers().getValues().get(0));
                if (newPlayer == null) return;

                PlayerNameTag.get(event.getPlayer()).addViewer(newPlayer);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
            LaBoulangerieCore.PLUGIN, ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_DESTROY
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                event.getPacket().getIntLists().getValues().get(0).stream().filter(id ->
                    idToPlayer.get(id) != null
                ).map(idToPlayer::get).forEach(p -> PlayerNameTag.get(p).removeViewer(event.getPlayer()));
            }
        });
    }

    public void disable() {
        textUpdateTask.cancel();
        PlayerNameTag.nameTags.forEach(PlayerNameTag::destroy);
        PlayerNameTag.nameTags.clear();
    }
}
