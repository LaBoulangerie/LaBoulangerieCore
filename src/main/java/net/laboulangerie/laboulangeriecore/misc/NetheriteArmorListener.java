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

    @EventHandler(priority = EventPriority.HIGH)
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        if (!isEnabled()) return;
        ItemStack armor = event.getNewItem();
        if (isUnmodifiedVanillaNetherite(armor)) {
            applySlowdownModifier(armor);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isEnabled()) return;
        ItemStack item = event.getCurrentItem();
        if (isUnmodifiedVanillaNetherite(item)) {
            applySlowdownModifier(item);
        }
        // Vérifier aussi le curseur (item déplacé)
        ItemStack cursor = event.getCursor();
        if (isUnmodifiedVanillaNetherite(cursor)) {
            applySlowdownModifier(cursor);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof Player)) return;

        ItemStack item = event.getItem().getItemStack();
        if (isUnmodifiedVanillaNetherite(item)) {
            applySlowdownModifier(item);
            event.getItem().setItemStack(item);
        }
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

    private void applySlowdownModifier(ItemStack item) {
        Material type = item.getType();
        double[] stats = VANILLA_STATS.get(type);
        EquipmentSlotGroup slotGroup = SLOT_GROUPS.get(type);

        ItemMeta meta = item.getItemMeta();

        // Armor
        meta.addAttributeModifier(Attribute.ARMOR,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "armor"),
                stats[0],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Toughness
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "toughness"),
                stats[1],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Knockback Resistance
        meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "kb"),
                stats[2],
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
            )
        );

        // Speed Slowdown (-10%)
        double speedModifier = LaBoulangerieCore.PLUGIN.getConfig()
            .getDouble("netherite-slowdown.speed-modifier", -0.1);

        meta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
            new AttributeModifier(
                new NamespacedKey(LaBoulangerieCore.PLUGIN, "speed"),
                speedModifier,
                AttributeModifier.Operation.ADD_SCALAR, // = add_multiplied_base
                slotGroup
            )
        );

        item.setItemMeta(meta);
    }
}
