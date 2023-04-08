package net.laboulangerie.laboulangeriecore.core.nametag;

import java.lang.reflect.Method;

import org.bukkit.World;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.nms.NMS;
import net.laboulangerie.laboulangeriecore.nms.NMSEntities;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityMetadata;
import net.laboulangerie.laboulangeriecore.nms.NMSSpawnEntityLiving;

public class ArmorStandEntity extends NMSEntities {

    private boolean shouldBeDisplayed = true;
    private boolean wasHidden = true;

    public ArmorStandEntity(World world, EntityType type, Object... parameters) {
        super(world, type, parameters);
        try {
            Method setCustomNameVisible = entity.getClass().getMethod("n", boolean.class);
            Method setSmall = entity.getClass().getMethod("a", boolean.class);
            Method setNoBasePlate = entity.getClass().getMethod("s", boolean.class);
            Method setMarker = entity.getClass().getMethod("t", boolean.class);
            Method setInvisible = entity.getClass().getMethod("j", boolean.class);

            setCustomNameVisible.invoke(entity, true);
            setSmall.invoke(entity, true);
            setNoBasePlate.invoke(entity, true);
            setMarker.invoke(entity, true);
            setInvisible.invoke(entity, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setText(Component component) {
        if (PlainTextComponentSerializer.plainText().serialize(component).length() == 0) {
            shouldBeDisplayed = false;
            return;
        }
        try {
            Class<?> chatBaseComponentClass = NMS.getClass("net.minecraft.network.chat.IChatBaseComponent");
            Class<?> chatBaseComponentSerializerClass = chatBaseComponentClass.getDeclaredClasses()[0];

            Method fromJson = chatBaseComponentSerializerClass.getMethod("a", String.class);
            Method setCustomName = entity.getClass().getMethod("b", chatBaseComponentClass);
            String jsonComponent = GsonComponentSerializer.gson().serialize(component);

            Object NMSComponent = fromJson.invoke(null, jsonComponent);

            setCustomName.invoke(entity, NMSComponent);
            shouldBeDisplayed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawn(Player target) {
        if (!shouldBeDisplayed) return;
        try {
            NMSSpawnEntityLiving.send(target, this);
            NMSEntityMetadata.send(target, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMetadata(Player target) {
        if (wasHidden) spawn(target);
        try {
            NMSEntityMetadata.send(target, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean shouldBeDisplayed() {
        return shouldBeDisplayed;
    }
}
