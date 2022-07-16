package net.laboulangerie.laboulangeriecore.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NMSSpawnEntityLiving {

    /**
     * Spawn a new entity
     *
     * @param player the player that will receive the packet
     * @param entity the entity that will be spawned
     */
    public static void send(Player player, NMSEntities entity) {
        try {
            final Class<?> packetClass = NMS.getClass("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
            final Class<?> entityLivingClass = NMS.getClass("net.minecraft.world.entity.EntityLiving");

            final Constructor<?> packetConstructor = packetClass.getConstructor(entityLivingClass);

            final Object packet = packetConstructor.newInstance(entity.entity);

            NMS.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}