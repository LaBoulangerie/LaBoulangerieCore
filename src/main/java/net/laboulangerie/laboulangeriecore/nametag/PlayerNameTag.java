package net.laboulangerie.laboulangeriecore.nametag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.laboulangerie.laboulangeriecore.nms.NMS;
import net.laboulangerie.laboulangeriecore.nms.NMSEntities;
import net.laboulangerie.laboulangeriecore.nms.NMSEntityMetadata;
import net.laboulangerie.laboulangeriecore.nms.NMSSpawnEntityLiving;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerNameTag {

    public static List<PlayerNameTag> nameTags = new ArrayList<>();

    private final Map<Integer, Boolean> tagsVisible;
    private final NMSEntities above;
    private final NMSEntities nameTag;
    private final NMSEntities below;
    private final Player player;

    public PlayerNameTag(@Nonnull Player player) {
        this.tagsVisible = new HashMap<>();
        this.player = player;
        this.above = new NMSEntities(player.getWorld(), NMSEntities.EntityType.ARMOR_STAND, player.getLocation().getX(), player.getLocation().getY() + 2.4, player.getLocation().getZ());
        this.nameTag = new NMSEntities(player.getWorld(), NMSEntities.EntityType.ARMOR_STAND, player.getLocation().getX(), player.getLocation().getY() + 2.1, player.getLocation().getZ());
        this.below = new NMSEntities(player.getWorld(), NMSEntities.EntityType.ARMOR_STAND, player.getLocation().getX(), player.getLocation().getY() + 1.8, player.getLocation().getZ());
    }

    public @Nonnull Player getPlayer() {
        return player;
    }

    public @Nonnull NMSEntities getAbove() {
        return above;
    }

    public @Nonnull NMSEntities getNameTag() {
        return nameTag;
    }

    public @Nonnull NMSEntities getBelow() {
        return below;
    }

    public @Nonnull Map<Integer, Boolean> getTagsVisible() {
        return tagsVisible;
    }

    public void spawnNameTag(@Nonnull Player player, @Nonnull NMSEntities entity, @Nonnull Component component) {
        if (player.getUniqueId().equals(this.player.getUniqueId())) return;

        try {
            final Class<?> chatBaseComponentClass = NMS.getClass("net.minecraft.network.chat.IChatBaseComponent");
            final Class<?> chatBaseComponentSerializerClass = chatBaseComponentClass.getDeclaredClasses()[0];

            final Method fromJson = chatBaseComponentSerializerClass.getMethod("a", String.class);
            final Method setCustomName = entity.getEntity().getClass().getMethod("a", chatBaseComponentClass);
            final Method setCustomNameVisible = entity.getEntity().getClass().getMethod("n", boolean.class);
            final Method setSmall = entity.getEntity().getClass().getMethod("a", boolean.class);
            final Method setNoBasePlate = entity.getEntity().getClass().getMethod("s", boolean.class);
            final Method setMarker = entity.getEntity().getClass().getMethod("t", boolean.class);
            final Method setInvisible = entity.getEntity().getClass().getMethod("j", boolean.class);

            final String json = GsonComponentSerializer.gson().serialize(component);

            final Object name = fromJson.invoke(null, json);

            setCustomName.invoke(entity.getEntity(), name);
            setCustomNameVisible.invoke(entity.getEntity(), !player.isInvisible());
            setSmall.invoke(entity.getEntity(), true);
            setNoBasePlate.invoke(entity.getEntity(), true);
            setMarker.invoke(entity.getEntity(), true);
            setInvisible.invoke(entity.getEntity(), true);

            NMSSpawnEntityLiving.send(player, entity);
            NMSEntityMetadata.send(player, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PlayerNameTag get(Player player) {
        for (PlayerNameTag p : nameTags)
            if (p.player.getUniqueId().equals(player.getUniqueId()))
                return (p);
        return (null);
    }
}
