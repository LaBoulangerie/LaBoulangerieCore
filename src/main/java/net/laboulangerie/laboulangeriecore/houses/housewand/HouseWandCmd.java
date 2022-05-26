package net.laboulangerie.laboulangeriecore.houses.housewand;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class HouseWandCmd implements CommandExecutor {

    private void giveWand(Player player) {
        if (player == null) return;

        ItemStack stack = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(69420);
        meta.setDisplayName("§6House wand");
        stack.setItemMeta(meta);
        player.getInventory().addItem(stack);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Only players can use this command!");
            return true;
        }

        giveWand(((Player) sender).getPlayer());
        return true;
    }
}
