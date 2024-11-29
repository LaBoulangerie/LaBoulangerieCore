package net.laboulangerie.laboulangeriecore.authenticate;

import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Authenticable {
    private ItemStack item;
    private LandsIntegration api = LandsIntegration.of(this);
    
    public Authenticable(ItemStack item) {
        this.item = item;
    }

    public boolean isAuthenticated() {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(LaBoulangerieCore.PLUGIN, "authority"), PersistentDataType.STRING) != null;
    }

    /**
     * If the value is present but invalid for whatever reason, it will return AuthorityType.INVALID
     */
    public AuthorityType getAuthorityType() {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't retrieve authority type!");

        String value = getValue();
        return value.startsWith("p") ? AuthorityType.PLAYER
                : value.startsWith("t") ? AuthorityType.TOWN
                : value.startsWith("n") ? AuthorityType.NATION
                : AuthorityType.INVALID;
    }

    /**
     * @return the raw value of the <b>authority</b> field
     */
    public String getValue() {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't retrieve authority value");
        return item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(LaBoulangerieCore.PLUGIN, "authority"), PersistentDataType.STRING);
    }

    public UUID getAuthorityUUID() {
        return UUID.fromString(getValue().substring(1));
    }

    public String getAuthorityName() {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't retrieve authority name");
        String lore = PlainTextComponentSerializer.plainText().serialize(item.lore().get(item.lore().size() - 1));
        return lore.split(" ")[2];
    }

    public void updateAuthorityName(String newName) {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't update authority name");
        List<Component> lore = item.lore();
        lore.set(item.lore().size() - 1, Component.text(newName));
        item.lore(lore);
    }

    public static String parseLore(String authority, AuthorityType type) {
        return LaBoulangerieCore.PLUGIN.getConfig().getString("authenticate.lore").replaceAll("%authority%", authority)
                .replaceAll("%type%", type.getSuffix());
    }
}