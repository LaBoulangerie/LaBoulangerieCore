package net.laboulangerie.laboulangeriecore.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NMSEntityMetadata {

    /**
     * Edit an entity metadata
     *
     * @param player the player that will receive the packet
     * @param entity the entity updated
     */
    public static void send(Player player, NMSEntities entity) {
        try {
            final Class<?> packetClass =
                    NMS.getClass("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
            final Class<?> dataWatcherClass = NMS.getClass("net.minecraft.network.syncher.DataWatcher");

            final Constructor<?> packetConstructor =
                    packetClass.getConstructor(int.class, dataWatcherClass, boolean.class);

            final Object packet = packetConstructor.newInstance(entity.getID(), entity.getDataWatcher(), true);

            NMS.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
