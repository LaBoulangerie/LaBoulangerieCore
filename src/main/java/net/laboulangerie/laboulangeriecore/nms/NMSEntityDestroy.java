package net.laboulangerie.laboulangeriecore.nms;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NMSEntityDestroy {

    /**
     * Remove an entity for a player
     *
     * @param player the player that will receive the packet
     * @param id the entity ID
     */
    public static void send(Player player, int... id) {
        try {
            final Class<?> packetClass = NMS.getClass("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");

            final Constructor<?> packetConstructor = packetClass.getConstructor(IntList.class);

            final Object packet = packetConstructor.newInstance(new IntArrayList(id));

            NMS.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
