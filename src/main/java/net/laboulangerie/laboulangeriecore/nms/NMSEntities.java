package net.laboulangerie.laboulangeriecore.nms;

import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NMSEntities {
    protected Object entity;
    protected int networkID;

    public enum EntityType {
        ARMOR_STAND(0x4E,"net.minecraft.world.entity.decoration.EntityArmorStand", NMS.getClass("net.minecraft.world.level.World"), double.class, double.class, double.class);

        public final int networkID;
        public final String className;
        public final Class<?>[] constructorArgs;

        EntityType(int networkID, String className, Class<?>... constructorArgs) {
            this.networkID = networkID;
            this.className = className;
            this.constructorArgs = constructorArgs;
        }
    }

    /**
     * Allow you to get the entity network ID
     *
     * @return the entity network ID
     */
    public int getNetworkID() {
        try {
            return networkID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Allow you to get the entity object
     *
     * @return the entity object
     */
    public Object getEntity() {
        return entity;
    }

    /**
     * Allow you to get the entity ID
     *
     * @return the entity ID
     */
    public int getID() {
        try {
            final Method getId = entity.getClass().getMethod("ae");
            return (int) getId.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Allow you to get the DataWatcher
     *
     * @return the entity DataWatcher
     */
    public Object getDataWatcher() {
        try {
            final Method getDataWatcher = entity.getClass().getMethod("ai");
            return getDataWatcher.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new entity instance
     *
     * @param world the world used to spawn the entity
     * @param type the type of entity that will be spawned
     */
    public NMSEntities(World world, EntityType type, Object... parameters) {
        try {
            final Class<?> entityClass = NMS.getClass(type.className);

            final Constructor<?> entityConstructor = entityClass.getConstructor(type.constructorArgs);
            final Object handle = world.getClass().getMethod("getHandle").invoke(world);

            final List<Object> tmp = new ArrayList<>();
            tmp.add(handle);
            tmp.addAll(Arrays.asList(parameters));

            final Object[] args = tmp.toArray();

            entity = entityConstructor.newInstance(args);
            networkID = type.networkID;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
