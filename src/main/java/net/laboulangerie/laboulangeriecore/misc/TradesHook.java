package net.laboulangerie.laboulangeriecore.misc;

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
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class TradesHook implements Listener {
    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT)
            return;

        Merchant merchant = ((MerchantInventory) event.getInventory()).getMerchant();

        List<MerchantRecipe> newRecipes = merchant.getRecipes().stream().map(recipe -> {
            MerchantRecipe newRecipe = new MerchantRecipe((
                    recipe.getResult().getType() == Material.EMERALD
                    ? new ItemStack(Material.BREAD, recipe.getResult().getAmount())
                    : recipe.getResult()
                ),
                recipe.getUses(), recipe.getMaxUses(),
                recipe.hasExperienceReward(), recipe.getVillagerExperience(),
                recipe.getPriceMultiplier()
            );
            newRecipe.setIngredients(recipe.getIngredients().stream().map(ingredient -> {
                if (ingredient.getType() == Material.EMERALD) {
                    ItemMeta meta = ingredient.getItemMeta();
                    ingredient.setType(Material.CLOCK);
                    meta.setCustomModelData(1);
                    meta.displayName(Component.text("§fPièce Brioche"));
                    ingredient.setItemMeta(meta);
                    ingredient.setAmount(ingredient.getAmount() * 2);
                }
                return ingredient;
            }).collect(Collectors.toList()));

            return newRecipe;
        }).collect(Collectors.toList());

        merchant.setRecipes(newRecipes);
    }
}
