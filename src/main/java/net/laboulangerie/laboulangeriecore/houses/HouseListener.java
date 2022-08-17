
package net.laboulangerie.laboulangeriecore.houses;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HouseListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakBlock(BlockBreakEvent event) {
        if (!event.isCancelled()) return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getBlock().getLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_BREAK)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!event.isCancelled()) return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getBlock().getLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_BUILD)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceItemFrame(HangingPlaceEvent event) {
        if (!event.isCancelled()) return;

        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveItemFrame(HangingBreakByEntityEvent event) {
        if (!event.isCancelled() || !(event.getRemover() instanceof Player)) return;
        
        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getRemover().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeItemFrame(PlayerInteractAtEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND || !event.isCancelled() && (event.getRightClicked().getType() != EntityType.ITEM_FRAME || event.getRightClicked().getType() != EntityType.ITEM_FRAME)) return;
        
        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (
            !house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId())
            || (
                (event.getRightClicked().getType() != EntityType.ITEM_FRAME || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS)) &&
                (event.getRightClicked().getType() != EntityType.ARMOR_STAND || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS))
            )) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeItemFrame(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND || !event.isCancelled() || event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;

        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_HANGINGS)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeArmorStand(PlayerArmorStandManipulateEvent event) {
        if (!event.isCancelled()) return;
        
        Location loc = event.getRightClicked().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSetArmorStand(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY && event.getClickedBlock() != null && (
            event.getClickedBlock().getType() == Material.LEVER ||
            event.getClickedBlock().getType().toString().endsWith("_BUTTON") ||
            event.getClickedBlock().getType().toString().endsWith("PRESSURE_PLATE") ||
            event.getClickedBlock().getType().toString().endsWith("_DOOR") ||
            event.getClickedBlock().getType().toString().endsWith("_TRAPDOOR") ||
            event.getClickedBlock().getType().toString().endsWith("_FENCE_GATE")
        )) {
            Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getClickedBlock().getLocation());

            if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_FLICK)) return;

            event.setCancelled(false);
            return;
        }
        if (event.useItemInHand() != Event.Result.DENY || event.getMaterial() != Material.ARMOR_STAND) return;

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(event.getInteractionPoint().toBlockLocation());

        if (!house.isPresent() || !house.get().hasMember(event.getPlayer().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS)) return;

        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() || !(event.getDamager() instanceof Player) || (event.getEntity().getType() != EntityType.ARMOR_STAND && event.getEntity().getType() != EntityType.ITEM_FRAME)) return;

        Location loc = event.getEntity().getLocation().toBlockLocation();
        loc.setPitch(0); // The entity's position contains decimals and orientation info, we get ride of those
        loc.setYaw(0);

        Optional<House> house = LaBoulangerieCore.housesManager.getHouseAt(loc);

        if (!house.isPresent() || !house.get().hasMember(event.getDamager().getUniqueId()) || !house.get().hasFlag(HouseFlags.CAN_SET_ARMOR_STANDS)) return;

        event.setCancelled(false);
    }
}