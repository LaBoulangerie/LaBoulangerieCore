package net.laboulangerie.laboulangeriecore.eco;

import static org.gestern.gringotts.Configuration.CONF;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.gestern.gringotts.currency.Denomination;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ConversionInv implements Listener {
    private static Merchant merchant = null;

    public static void displayConversionInv(Player player) {
        if (merchant != null) {
            player.openMerchant(merchant, true);
            return;
        }
        List<Denomination> denominations =  CONF.getCurrency().getDenominations();

        merchant = Bukkit.createMerchant(Component.text("§bConvertisseur de monnaie"));
        List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();

        ItemStack brioche = denominations.get(2).getKey().type;
        brioche.setAmount(1);
        ItemStack croissant = denominations.get(1).getKey().type;
        croissant.setAmount(1);
        ItemStack baguette = denominations.get(0).getKey().type;
        baguette.setAmount(1);

        // 100*Brioche = 1*Croissant
        MerchantRecipe briocheToCroissant = new MerchantRecipe(croissant.clone(), 10000); // no max-uses limit
        briocheToCroissant.setExperienceReward(false); // no experience rewards
        brioche.setAmount(64);
        briocheToCroissant.addIngredient(brioche.clone());
        brioche.setAmount(36);
        briocheToCroissant.addIngredient(brioche.clone());

        // 10*Croissant = 1*Baguette
        MerchantRecipe croissantToBaguette = new MerchantRecipe(baguette.clone(), 10000); // no max-uses limit
        croissantToBaguette.setExperienceReward(false); // no experience rewards
        croissant.setAmount(10);
        croissantToBaguette.addIngredient(croissant.clone());

        // 1*Baguette = 10*Croissant
        croissant.setAmount(10);
        MerchantRecipe baguetteToCroissant = new MerchantRecipe(croissant.clone(), 10000); // no max-uses limit
        baguetteToCroissant.setExperienceReward(false); // no experience rewards
        baguetteToCroissant.addIngredient(baguette.clone());

        // 1*Croissant = 100*Brioche

        brioche.setAmount(100);
        MerchantRecipe croissantToBrioche = new MerchantRecipe(brioche.clone(), 10000); // no max-uses limit
        croissantToBrioche.setExperienceReward(false); // no experience rewards
        croissant.setAmount(1);
        croissantToBrioche.addIngredient(croissant.clone());

        merchantRecipes.add(briocheToCroissant);
        merchantRecipes.add(croissantToBaguette);
        merchantRecipes.add(baguetteToCroissant);
        merchantRecipes.add(croissantToBrioche);
        merchant.setRecipes(merchantRecipes);

        // open trading window:
        player.openMerchant(merchant, true);
    }

    @EventHandler
    public void onTrade(TradeSelectEvent event) {
        if (PlainTextComponentSerializer.plainText().serialize(event.getWhoClicked().getOpenInventory().title()).contains("Convertisseur de monnaie"))
            event.getInventory().setMaxStackSize(127);
    }
}
