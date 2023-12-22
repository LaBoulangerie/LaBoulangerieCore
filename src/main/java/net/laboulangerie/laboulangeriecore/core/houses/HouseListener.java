
package net.laboulangerie.laboulangeriecore.core.houses;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.util.Util;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HouseListener implements Listener {
    private Optional<QuickShopAPI> quickShop = Optional.empty();

    public HouseListener() {
        if (Bukkit.getServer().getPluginManager().getPlugin("QuickShop") != null) {
            quickShop = Optional.of(QuickShopAPI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakBlock(BlockBreakEvent event) {
        if (!event.isCancelled())
            return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getBlock().getLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_BREAK))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!event.isCancelled())
            return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getBlock().getLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_BUILD))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceItemFrame(HangingPlaceEvent event) {
        if (!event.isCancelled())
            return;

        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveItemFrame(HangingBreakByEntityEvent event) {
        if (!event.isCancelled() || !(event.getRemover() instanceof Player))
            return;

        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getRemover().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeItemFrame(PlayerInteractAtEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND
                || !event.isCancelled() && (event.getRightClicked().getType() != EntityType.ITEM_FRAME
                        || event.getRightClicked().getType() != EntityType.ITEM_FRAME))
            return;

        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || ((event.getRightClicked().getType() != EntityType.ITEM_FRAME
                        || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS))
                        && (event.getRightClicked().getType() != EntityType.ARMOR_STAND
                                || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS))))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeItemFrame(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND || !event.isCancelled()
                || event.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;

        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeArmorStand(PlayerArmorStandManipulateEvent event) {
        if (!event.isCancelled())
            return;

        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS))
            return;

        event.setCancelled(false);
    }

    /**
     * CAN_FLICK CAN_SET_FLICK
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteraction(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.useInteractedBlock() == Event.Result.DENY && block != null
                && (block.getType() == Material.LEVER || block.getType().toString().endsWith("_BUTTON")
                        || block.getType().toString().endsWith("PRESSURE_PLATE")
                        || block.getType().toString().endsWith("_DOOR")
                        || block.getType().toString().endsWith("_TRAPDOOR")
                        || block.getType().toString().endsWith("_FENCE_GATE")
                        || (quickShop.isPresent() && Util.canBeShop(block)))) {
            Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(block.getLocation());

            if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()))
                return;

            if ((block.getType() == Material.LEVER || block.getType().toString().endsWith("_BUTTON")
                    || block.getType().toString().endsWith("PRESSURE_PLATE")
                    || block.getType().toString().endsWith("_DOOR") || block.getType().toString().endsWith("_TRAPDOOR")
                    || block.getType().toString().endsWith("_FENCE_GATE")
                            && house.get().hasFlag(HouseFlags.CAN_FLICK))) {
                event.setCancelled(false);
            } else if (quickShop.get().getShopManager().getShop(block.getLocation()) != null
                    && house.get().hasFlag(HouseFlags.SHOPS_ALLOWED))
                event.setCancelled(false);
            return;
        }
        if (event.useItemInHand() != Event.Result.DENY || event.getMaterial() != Material.ARMOR_STAND)
            return;

        Optional<House> house = LaBoulangerieCore.housesManager
                .getHouseAt(event.getInteractionPoint().toBlockLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() || !(event.getDamager() instanceof Player)
                || (event.getEntity().getType() != EntityType.ARMOR_STAND
                        && event.getEntity().getType() != EntityType.ITEM_FRAME))
            return;

        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get rid of
                         // those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getDamager().getUniqueId())
                || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS))
            return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenInventory(InventoryOpenEvent event) {
        if (!event.isCancelled() || !(event.getInventory().getHolder() instanceof BlockInventoryHolder))
            return;

        Block block = ((BlockInventoryHolder) event.getInventory().getHolder()).getBlock();

        if (quickShop.isEmpty() || !Util.canBeShop(block))
            return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(block.getLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
                || !house.get().hasFlag(HouseFlags.SHOPS_ALLOWED)
                || quickShop.get().getShopManager().getShop(block.getLocation()) == null)
            return;

        event.setCancelled(false);
    }
}
