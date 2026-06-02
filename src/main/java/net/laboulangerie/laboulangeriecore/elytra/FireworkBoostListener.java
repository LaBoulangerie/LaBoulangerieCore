package net.laboulangerie.laboulangeriecore.elytra;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class FireworkBoostListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onElytraBoost(PlayerElytraBoostEvent event) {
        if (!isEnabled()) return;

        if (hasValidLore(event.getItemStack())) return;

        event.setCancelled(true);
        event.setShouldConsume(false);
        sendDeniedMessage(event.getPlayer());
    }

    private boolean hasValidLore(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;

        String requiredLore = getRequiredLore();
        for (var loreLine : meta.lore()) {
            String text = PlainTextComponentSerializer.plainText().serialize(loreLine);
            if (text.equals(requiredLore)) return true;
        }
        return false;
    }

    private void sendDeniedMessage(Player player) {
        String message = LaBoulangerieCore.PLUGIN.getConfig()
            .getString("firework-boost.denied-message", "<red>Cette fusée ne peut pas vous propulser !");
        player.sendActionBar(MiniMessage.miniMessage().deserialize(message));
    }

    private boolean isEnabled() {
        return LaBoulangerieCore.PLUGIN.getConfig().getBoolean("firework-boost.enabled", true);
    }

    private String getRequiredLore() {
        return LaBoulangerieCore.PLUGIN.getConfig().getString("firework-boost.required-lore", "Propulseur");
    }
}
