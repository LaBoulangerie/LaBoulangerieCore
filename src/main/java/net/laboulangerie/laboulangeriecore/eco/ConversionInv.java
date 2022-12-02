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
    public static void displayConversionInv(Player player) {
        List<Denomination> denominations =  CONF.getCurrency().getDenominations();

        Merchant merchant = Bukkit.createMerchant(Component.text("§bConvertisseur de monnaie"));
        List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();

        for (int i = denominations.size()-1; i > 0; i--) {
            int y = i - 1;

            ItemStack needed = denominations.get(i).getKey().type.clone();
            int amount = (int) (denominations.get(y).getValue() / denominations.get(i).getValue());
            ItemStack result = denominations.get(y).getKey().type.clone();
            result.setAmount(1);

            MerchantRecipe recipe = new MerchantRecipe(result, 10000); // no max-uses limit
            recipe.setExperienceReward(false); // no experience rewards
            if (amount > 64) {
                needed.setAmount(64);
                recipe.addIngredient(needed.clone());
                amount -= 64;
            }
            needed.setAmount(amount);
            recipe.addIngredient(needed);
            merchantRecipes.add(recipe);
        }
        for (int i = 0; i < denominations.size()-1; i++) {
            int y = i + 1;

            ItemStack needed = denominations.get(i).getKey().type.clone();
            needed.setAmount(1);
            ItemStack result = denominations.get(y).getKey().type.clone();
            result.setAmount((int) (denominations.get(i).getValue() / denominations.get(y).getValue()));

            MerchantRecipe recipe = new MerchantRecipe(result, 10000); // no max-uses limit
            recipe.setExperienceReward(false); // no experience rewards
            recipe.addIngredient(needed);
            merchantRecipes.add(recipe);
        }

        merchant.setRecipes(merchantRecipes);
        player.openMerchant(merchant, true);
    }

    @EventHandler
    public void onTrade(TradeSelectEvent event) {
        if (PlainTextComponentSerializer.plainText().serialize(event.getWhoClicked().getOpenInventory().title()).contains("Convertisseur de monnaie"))
            event.getInventory().setMaxStackSize(127);
    }
}
