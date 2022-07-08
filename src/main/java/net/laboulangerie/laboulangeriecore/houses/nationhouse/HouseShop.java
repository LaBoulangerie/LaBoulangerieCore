package net.laboulangerie.laboulangeriecore.houses.nationhouse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;

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
import net.laboulangerie.laboulangeriecore.houses.House;

public class HouseShop implements Listener {
    public static void displayShop(Player player, short page) {
        int invSize = 27;
        Inventory inv = Bukkit.createInventory(null, invSize, Component.text("§2Maisons de nation - page " + (page+1)));
        List<UUID> freeHouses = LaBoulangerieCore.nationHouseHolder.getFreeHouses();

        for (int i = page*(invSize-2); i < Math.min(freeHouses.size(), (page+1) * (invSize-2)); i++) {
            UUID id = freeHouses.get(i);
            ItemStack item = new ItemStack(Material.BRICKS);
            ItemMeta meta = item.getItemMeta();
            House house = LaBoulangerieCore.housesManager.getHouse(id);

            meta.displayName(Component.text("§6" + house.getName()));
            meta.lore(Arrays.asList(
                Component.text("§bPrix : " + LaBoulangerieCore.nationHouseHolder.getHousePrice(id)),
                Component.text(
                    "§5Location : " + house.getAnchor().getX() + ", "
                    + house.getAnchor().getY() + ", " + house.getAnchor().getZ()
                )
            ));

            item.setItemMeta(meta);
            inv.setItem(i - page*(invSize-2), item);
        }
        if (page > 0) {
            ItemStack leftArrow = new ItemStack(Material.ARROW);
            ItemMeta meta = leftArrow.getItemMeta();
            meta.setCustomModelData(1);
            meta.displayName(Component.text("§7Précédent"));
            leftArrow.setItemMeta(meta);

            inv.setItem((invSize-2), leftArrow);
        }
        if (freeHouses.size() > (page+1) * (invSize-2)) {
            ItemStack rightArrow = new ItemStack(Material.ARROW);
            ItemMeta meta = rightArrow.getItemMeta();
            meta.setCustomModelData(2);
            meta.displayName(Component.text("§7Suivant"));
            rightArrow.setItemMeta(meta);

            inv.setItem((invSize-1), rightArrow);
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
        Player player = (Player) event.getWhoClicked();

        if (item.getType() == Material.ARROW) {
            if (item.getItemMeta().getCustomModelData() == 2) { //Right arrow is clicked
                displayShop(player, page); //No need for page++; because page's title is aldready increased by 1 to prevent displaying "page 0"
            }else { //Left arrow is clicked (CustomModelData = 2)
                page-=2; //2 accounts for the increased page's title (see before)
                displayShop(player, page);
            }
            return;
        }

        if (item.getType() != Material.BRICKS) return;

        double price = 0;
        try {
            String priceStr = PlainTextComponentSerializer.plainText().serialize(
                item.getItemMeta().lore().get(0)
            ).split(" : ")[1];
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            player.sendMessage("§4An error occurred when trying to buy the house: " + e.getMessage());
            return;
        }

        Nation nation = null;
        try {
            nation = TownyUniverse.getInstance().getResident(player.getUniqueId()).getNation();
        } catch (TownyException e) {
        } finally {
            if (nation == null) {
                player.sendMessage("§cVous n'avez pas de nation !");
                return;
            }
        }
        if (LaBoulangerieCore.nationHouseHolder.hasHouse(nation.getUUID())) {
            player.sendMessage("§cVotre nation a déjà une maison de nation !");
            return;
        }
        if (!LaBoulangerieCore.econ.has(player, price)) {
            player.sendMessage("§cVous n'avez pas assez d'argent !");
            return;
        }

        String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName()).split("§6")[1];
        House house = LaBoulangerieCore.housesManager.getHouseByName(name).get();

        LaBoulangerieCore.nationHouseHolder.assignNationHouse(house.getUUID(), nation.getUUID());
        LaBoulangerieCore.econ.withdrawPlayer(player, price);
        player.sendMessage(
            "§aVous avez acheté la maison : §b" + house.getName() + "§a (§5"
            + house.getAnchor().getX() + ", "
            + house.getAnchor().getY() + ", "
            + house.getAnchor().getZ() + "§a)"
        );
        player.closeInventory();
        house.addMember(player.getUniqueId());
    }
}
