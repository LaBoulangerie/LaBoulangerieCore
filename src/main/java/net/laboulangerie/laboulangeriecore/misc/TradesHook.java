package net.laboulangerie.laboulangeriecore.misc;

import static org.gestern.gringotts.Configuration.CONF;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class TradesHook implements Listener {
    private final int EMERALD_VALUE = LaBoulangerieCore.PLUGIN.getConfig().getInt("emerald-value");
    private ItemStack currency = CONF.getCurrency().getDenominations().get(CONF.getCurrency().getDenominations().size()-1).getKey().type; //Currencies are stocked by descending value

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;

        Merchant merchant = ((MerchantInventory) event.getInventory()).getMerchant();

        List<MerchantRecipe> newRecipes = merchant.getRecipes().stream().filter(recipe -> recipe.getResult().getType() != Material.EMERALD).map(recipe -> {
            currency.setAmount(recipe.getResult().getAmount() * EMERALD_VALUE);
            MerchantRecipe newRecipe = new MerchantRecipe(recipe.getResult(),
                recipe.getUses(), recipe.getMaxUses(),
                recipe.hasExperienceReward(), recipe.getVillagerExperience(),
                recipe.getPriceMultiplier()
            );
            newRecipe.setIngredients(recipe.getIngredients());

            ItemStack emerald = recipe.getIngredients().get(0);
            if (emerald.getType() != Material.EMERALD) return newRecipe;

            int quantity = emerald.getAmount() * EMERALD_VALUE;
            List<ItemStack> newIngredients = new ArrayList<>();

            currency.setAmount(quantity > 64 ? 64 : quantity);
            newIngredients.add(currency.clone());

            if (quantity > 64) { //if the currency amount doesn't fit in one slot, override the second slot with the currency amount lasting
                quantity -= 64;
                currency.setAmount(quantity > 64 ? 64 : quantity); //cap prices to 128 (corresponds to 24.5 emerald if EMERALD_VALUE = 5)
                newIngredients.add(currency.clone());
            }else if (recipe.getIngredients().size() == 2) newIngredients.add(recipe.getIngredients().get(1));

            newRecipe.setIngredients(newIngredients);

            return newRecipe;
        }).collect(Collectors.toList());

        merchant.setRecipes(newRecipes);
    }
}
