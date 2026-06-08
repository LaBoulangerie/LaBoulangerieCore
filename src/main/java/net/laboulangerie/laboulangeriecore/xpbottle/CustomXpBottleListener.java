package net.laboulangerie.laboulangeriecore.xpbottle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class CustomXpBottleListener implements Listener {

    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        if (!isEnabled()) return;

        ItemStack bottle = event.getEntity().getItem();
        ItemMeta meta = bottle.getItemMeta();

        if (meta == null || !meta.hasLore()) return;

        String prefix = getLorePrefix();

        for (var loreLine : meta.lore()) {
            String text = PlainTextComponentSerializer.plainText().serialize(loreLine);
            if (text.startsWith(prefix)) {
                try {
                    int expPoints = Integer.parseInt(text.substring(prefix.length()).trim());
                    if (expPoints > 0) {
                        event.setExperience(expPoints);
                    }
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    private boolean isEnabled() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getBoolean("xp-bottle.enabled", true);
    }

    private String getLorePrefix() {
        return LaBoulangerieCore.PLUGIN.getConfig()
            .getString("xp-bottle.lore-prefix", "xp: ");
    }
}
