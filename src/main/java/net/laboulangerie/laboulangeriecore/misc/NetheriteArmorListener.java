package net.laboulangerie.laboulangeriecore.misc;

import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class NetheriteArmorListener implements Listener {

    private static final Set<Material> NETHERITE_ARMOR = Set.of(
        Material.NETHERITE_HELMET,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_BOOTS
    );

    // Valeurs vanilla des attributs par pièce (armor, toughness, knockback_resistance)
    private static final Map<Material, double[]> VANILLA_STATS = Map.of(
        Material.NETHERITE_HELMET,     new double[]{3.0, 3.0, 0.1},
        Material.NETHERITE_CHESTPLATE, new double[]{8.0, 3.0, 0.1},
        Material.NETHERITE_LEGGINGS,   new double[]{6.0, 3.0, 0.1},
        Material.NETHERITE_BOOTS,      new double[]{3.0, 3.0, 0.1}
    );

    private static final Map<Material, EquipmentSlotGroup> SLOT_GROUPS = Map.of(
        Material.NETHERITE_HELMET,     EquipmentSlotGroup.HEAD,
        Material.NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST,
        Material.NETHERITE_LEGGINGS,   EquipmentSlotGroup.LEGS,
        Material.NETHERITE_BOOTS,      EquipmentSlotGroup.FEET
    );

    private String getSlotSuffix(Material type) {
        return switch (type) {
            case NETHERITE_HELMET -> "_head";
            case NETHERITE_CHESTPLATE -> "_chest";
            case NETHERITE_LEGGINGS -> "_legs";
            case NETHERITE_BOOTS -> "_feet";
            default -> "";
        };
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        if (!isEnabled()) return;
        ItemStack armor = event.getNewItem();
        processNetheriteArmor(armor);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isEnabled()) return;
        processNetheriteArmor(event.getCurrentItem());
        processNetheriteArmor(event.getCursor());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof Player)) return;

        ItemStack item = event.getItem().getItemStack();
        if (processNetheriteArmor(item)) {
            event.getItem().setItemStack(item);
        }
    }

    private boolean processNetheriteArmor(ItemStack item) {
        if (hasOldSpeedModifier(item)) {
            convertOldArmor(item);
            return true;
        } else if (isUnmodifiedVanillaNetherite(item)) {
            applySlowdownModifier(item);
            return true;
        }
        return false;
    }

    private boolean isEnabled() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getBoolean("netherite-slowdown.enabled", true);
    }

    private boolean isUnmodifiedVanillaNetherite(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        if (!NETHERITE_ARMOR.contains(item.getType())) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return true;

        // Vérifications: pas d'enchant, pas de nom, pas de lore, pas d'attributs custom
        return !meta.hasEnchants()
            && !meta.hasDisplayName()
            && !meta.hasLore()
            && !meta.hasAttributeModifiers();
    }

    private boolean hasOldSpeedModifier(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        if (!NETHERITE_ARMOR.contains(item.getType())) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasAttributeModifiers()) return false;

        var modifiers = meta.getAttributeModifiers(Attribute.MOVEMENT_SPEED);
        if (modifiers == null) return false;

        NamespacedKey oldKey = new NamespacedKey(LaBoulangerieCore.PLUGIN, "speed");
        for (AttributeModifier mod : modifiers) {
            if (mod.getKey().equals(oldKey)) {
                return true;
            }
        }
        return false;
    }

    private void convertOldArmor(ItemStack item) {
        Material type = item.getType();
        ItemMeta meta = item.getItemMeta();
        String suffix = getSlotSuffix(type);

        // Supprimer tous les anciens modifiers du plugin
        removeModifierByKey(meta, Attribute.MOVEMENT_SPEED, new NamespacedKey(LaBoulangerieCore.PLUGIN, "speed"));
        removeModifierByKey(meta, Attribute.ARMOR, new NamespacedKey(LaBoulangerieCore.PLUGIN, "armor" + suffix));
        removeModifierByKey(meta, Attribute.ARMOR_TOUGHNESS, new NamespacedKey(LaBoulangerieCore.PLUGIN, "toughness" + suffix));
        removeModifierByKey(meta, Attribute.KNOCKBACK_RESISTANCE, new NamespacedKey(LaBoulangerieCore.PLUGIN, "kb" + suffix));

        item.setItemMeta(meta);

        // Appliquer les nouveaux modifiers
        applySlowdownModifier(item);
    }

    private void removeModifierByKey(ItemMeta meta, Attribute attribute, NamespacedKey key) {
        var modifiers = meta.getAttributeModifiers(attribute);
        if (modifiers == null) return;

        for (AttributeModifier mod : modifiers) {
            if (mod.getKey().equals(key)) {
                meta.removeAttributeModifier(attribute, mod);
            }
        }
    }

    private void applySlowdownModifier(ItemStack item) {
        Material type = item.getType();
        double[] stats = VANILLA_STATS.get(type);
        EquipmentSlotGroup slotGroup = SLOT_GROUPS.get(type);
        String suffix = getSlotSuffix(type);

        ItemMeta meta = item.getItemMeta();

        // Armor (key unique par slot pour permettre le cumul)
        meta.addAttributeModifier(Attribute.ARMOR,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "armor" + suffix),
                stats[0],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Toughness (key unique par slot pour permettre le cumul)
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "toughness" + suffix),
                stats[1],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Knockback Resistance (key unique par slot pour permettre le cumul)
        meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "kb" + suffix),
                stats[2],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Speed Slowdown - divisé par 4 pour que le total avec 4 pièces = config value
        double speedModifier = LaBoulangerieCore.PLUGIN.getConfig()
            .getDouble("netherite-slowdown.speed-modifier", -0.1) / 4.0;

        meta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "speed" + suffix),
                speedModifier,
                AttributeModifier.Operation.ADD_SCALAR,
                slotGroup
            )
        );

        item.setItemMeta(meta);
    }
}
