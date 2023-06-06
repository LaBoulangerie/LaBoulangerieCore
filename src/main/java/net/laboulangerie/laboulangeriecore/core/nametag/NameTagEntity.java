package net.laboulangerie.laboulangeriecore.core.nametag;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import com.comphenix.net.bytebuddy.build.Plugin.Engine.Target;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class NameTagEntity {
    private boolean shouldBeDisplayed = true;
    private boolean wasHidden = true;
    private Location location;
    private Component text;
    private UUID uuid = UUID.randomUUID();
    private int id;

    private boolean isCrouching = false;

    public NameTagEntity(Location location, int id) {
        this.location = location;
        this.id = id;
        // try {
        //     Method setCustomNameVisible = entity.getClass().getMethod("n", boolean.class);
        //     //Method setSmall = entity.getClass().getMethod("a", boolean.class);
        //     //Method setNoBasePlate = entity.getClass().getMethod("s", boolean.class);
        //     //Method setMarker = entity.getClass().getMethod("t", boolean.class);
        //     Method setInvisible = entity.getClass().getMethod("j", boolean.class);

        //     setCustomNameVisible.invoke(entity, true);
        //     // setSmall.invoke(entity, true);
        //     // setNoBasePlate.invoke(entity, true);
        //     // setMarker.invoke(entity, true);
        //     setInvisible.invoke(entity, true);
        // } catch (Exception e) {
        //     e.printStackTrace();
    }

    public void setText(Component component) {
        if (PlainTextComponentSerializer.plainText().serialize(component).length() == 0) {
            shouldBeDisplayed = false;
            return;
        }
        this.text = component;
        // try {
        //     Class<?> chatBaseComponentClass = NMS.getClass("net.minecraft.network.chat.IChatBaseComponent");
        //     Class<?> chatBaseComponentSerializerClass = chatBaseComponentClass.getDeclaredClasses()[0];

        //     Method fromJson = chatBaseComponentSerializerClass.getMethod("a", String.class);
        //     Method setCustomName = entity.getClass().getMethod("b", chatBaseComponentClass);
        //     String jsonComponent = GsonComponentSerializer.gson().serialize(component);

        //     Object NMSComponent = fromJson.invoke(null, jsonComponent);

        //     setCustomName.invoke(entity, NMSComponent);
        //     shouldBeDisplayed = true;
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

    public void spawn(Player target) {
        if (!shouldBeDisplayed) return;

        PacketContainer spawnText = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        StructureModifier<Object> mods = spawnText.getModifier();

        mods.write(0, id) // Entity ID
            .write(1, uuid) // entity UUID
            .write(2, EntityType.TEXT_DISPLAY).write(3, location.getX()) // Pos X, Y, Z
            .write(4, location.getY() + 0.3).write(5, location.getZ())
            .write(6, (short) 0) // Velocity
                                                                    // X, Y, Z
            .write(7, (short) 0).write(8, (short) 0).write(9, (byte) 0) // pitch
            .write(10, (byte) 0) // yaw
            .write(11, (byte) 0); // yaw

        ProtocolLibrary.getProtocolManager().sendServerPacket(target, spawnText);
        wasHidden = false;
        sendMetadata(target);
    }

    public void sendMetadata(Player target) {
        if (wasHidden) spawn(target);
        PacketContainer textMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        textMetadata.getIntegers().write(0, id);

        // 0x20 = invisible, 0x02 = crouching
        textMetadata.getDataValueCollectionModifier().write(
            0,
            Arrays.asList(new WrappedDataValue(
                0,
                Registry.get(Byte.class), (byte) (0x20 | (isCrouching ? 0x02 : 0x0))
            )
        ));

        textMetadata.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(text)));
        ProtocolLibrary.getProtocolManager().sendServerPacket(target, textMetadata);
    }

    public boolean shouldBeDisplayed() {
        return shouldBeDisplayed;
    }

    public void destroy(Player target) {
        PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, Arrays.asList(id));

        ProtocolLibrary.getProtocolManager().sendServerPacket(target, destroy);
    }
}
