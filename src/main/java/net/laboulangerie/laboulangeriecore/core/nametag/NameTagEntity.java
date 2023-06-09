package net.laboulangerie.laboulangeriecore.core.nametag;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.joml.Vector3f;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class NameTagEntity {
    private boolean shouldBeDisplayed = true;
    private boolean wasHidden = true;
    private Location location;
    private Component text;
    private UUID uuid = UUID.randomUUID();
    private int id;
    private int ownerId;

    private boolean isCrouching = false;
    private boolean isVisible = true;

    public NameTagEntity(Location location, int id, int ownerId) {
        System.out.println(id);
        this.location = location;
        this.id = id;
        this.ownerId = ownerId;
    }

    public void setText(Component component) {
        // if (PlainTextComponentSerializer.plainText().serialize(component).length() == 0) {
        //     shouldBeDisplayed = false;
        //     return;
        // }
        this.text = component;
        shouldBeDisplayed = true;
    }

    public void spawn(Player... targets) {
        if (!shouldBeDisplayed) return;
        wasHidden = false;

        PacketContainer spawnText = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        StructureModifier<Object> mods = spawnText.getModifier();

        mods.write(0, id) // Entity ID
            .write(1, uuid) // entity UUID
            .write(2, BukkitConverters.getEntityTypeConverter().getGeneric(EntityType.TEXT_DISPLAY))
            .write(3, location.getX()).write(4, location.getY() - 1.3).write(5, location.getZ()) // Pos X, Y, Z
            .write(6, (short) 0) // Velocity
                                                                    // X, Y, Z
            .write(7, (short) 0).write(8, (short) 0).write(9, (byte) 0) // pitch
            .write(10, (byte) 0) // yaw
            .write(11, (byte) 0); // yaw

        PacketContainer setPassenger = new PacketContainer(PacketType.Play.Server.MOUNT);

        setPassenger.getIntegers()
            .write(0, ownerId);
        setPassenger.getIntegerArrays().write(0, new int[]{id});

        for (Player target : targets) {
            System.out.println("Sending to " + target.getName() + " from " + NameTagManager.idToPlayer.get(ownerId).getName());
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, spawnText);
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, setPassenger);
        }
        sendMetadata(targets);

    }

    public void sendMetadata(Player... targets) {
        if (wasHidden) spawn(targets);
        // System.out.println(ownerId); 
        PacketContainer textMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        textMetadata.getIntegers().write(0, id);
        textMetadata.getDataValueCollectionModifier().write(
            0,
            Arrays.asList(
                new WrappedDataValue(
                    0, // 0x20 = invisible, 0x02 = crouching
                    Registry.get(Byte.class), (byte) ((isVisible ? 0 : 0x20) | (isCrouching ? 0x02 : 0x0))
                ),
                new WrappedDataValue(
                    10,
                    Registry.get(Vector3f.class), new Vector3f(0.0F, 0.7F, 0.0F) // Translation
                ),
                new WrappedDataValue(
                    14,
                    Registry.get(Byte.class), (byte) 0x3 // Fixed center
                ),
                new WrappedDataValue(
                    15,
                    Registry.get(Integer.class), (5 << 4 | 5 << 20) // brightness
                ),
                new WrappedDataValue(
                    16,
                    Registry.get(Float.class), 0.5F // View range
                ),
                new WrappedDataValue(
                    24,
                    Registry.get(Integer.class), 0 // bg color
                ),
                new WrappedDataValue(
                    25,
                    Registry.get(Byte.class), (byte) -1 // Opacity
                ),
                new WrappedDataValue(
                   22,
                   Registry.getChatComponentSerializer(false), WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(text)).getHandle()
                ),
                new WrappedDataValue(
                    26,
                    Registry.get(Byte.class), (byte) (isCrouching ? 0 : 0x02) // See through blocks
                )
            ) 
        );

        for (Player target : targets){
            // System.out.println(ownerId + target.getName());
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, textMetadata);}
    }

    public boolean shouldBeDisplayed() {
        return shouldBeDisplayed;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setCrouching(boolean isCrouching) {
        this.isCrouching = isCrouching;
    }

    public void destroy(Player... targets) {
        PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, Arrays.asList(id));

        for (Player player : targets)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroy);
    }
}
