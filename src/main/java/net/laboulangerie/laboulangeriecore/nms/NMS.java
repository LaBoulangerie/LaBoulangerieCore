package net.laboulangerie.laboulangeriecore.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMS {

    /**
     * Retrieve a net.minecraft.server (NMS) class using its name
     *
     * @param name the name of the class
     * @return the class with the corresponding name
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("b").get(handle);
            final Object networkManager = playerConnection.getClass().getField("a").get(playerConnection);
            networkManager.getClass().getMethod("a", getClass("net.minecraft.network.protocol.Packet")).invoke(networkManager, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
