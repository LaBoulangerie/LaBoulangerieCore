package net.laboulangerie.laboulangeriecore.houses.nationhouse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class HouseShop implements Listener {
    public static void displayShop(Player player, short page) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("§6Maisons de nation - page " + (page+1)));
        List<UUID> freeHouses = LaBoulangerieCore.nationHouseHolder.getFreeHouses();

        for (int i = page*52; i < Math.min(freeHouses.size(), page*52 + 52); i++) {
            UUID id = freeHouses.get(i);
            ItemStack item = new ItemStack(Material.BRICKS);
            ItemMeta meta = item.getItemMeta();

            meta.displayName(Component.text("§6" + LaBoulangerieCore.housesManager.getHouse(id).getName()));
            meta.lore(Arrays.asList(Component.text("§bPrix : " + LaBoulangerieCore.nationHouseHolder.getHousePrice(id))));

            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        if (page > 0) {
            ItemStack leftArrow = new ItemStack(Material.ARROW);
            ItemMeta meta = leftArrow.getItemMeta();
            meta.setCustomModelData(1);
            leftArrow.setItemMeta(meta);

            inv.setItem(50, leftArrow);
        }
        if (freeHouses.size() >= 52) {
            ItemStack rightArrow = new ItemStack(Material.ARROW);
            ItemMeta meta = rightArrow.getItemMeta();
            meta.setCustomModelData(2);
            rightArrow.setItemMeta(meta);
    
            inv.setItem(51, rightArrow);
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = PlainTextComponentSerializer.plainText().serialize(view.title());
        if (!title.contains("Maisons de nation")) return;

        event.setCancelled(true);

        if (event.getClick() != ClickType.LEFT || event.getCurrentItem() == null) return;

        ItemStack item = event.getCurrentItem();
        short page = Short.parseShort(title.split("page ")[1]);

        if (item.getType() == Material.ARROW) {
            if (item.getItemMeta().getCustomModelData() == 2) { //Right arrow is clicked
                page++;
                displayShop((Player) event.getWhoClicked(), page);
            }else { //Left arrow is clicked (CustomModelData = 2)
                page--;
                displayShop((Player) event.getWhoClicked(), page);
            }
        }
    }
}
