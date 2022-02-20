package net.laboulangerie.laboulangeriecore.authenticate;

import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Authenticable {
    private ItemStack item;
    public Authenticable(ItemStack item) {
        this.item = item;
    }

    public boolean isAuthenticated() {
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(LaBoulangerieCore.PLUGIN, "authority"), PersistentDataType.STRING) != null;
    }

    /**
     * If the value is present but invalid for whatever reason, it will return AuthorityType.NATION
     */
    public AuthorityType getAuthorityType() {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't retrieve authority type!");

        String value = getValue();
        return value.startsWith("p") ? AuthorityType.PLAYER : value.startsWith("t") ? AuthorityType.TOWN : AuthorityType.NATION;
    }

    /**
     * @return the raw value of the <b>authority</b> field
     */
    public String getValue() {
        if (!isAuthenticated()) throw new Error("The item isn't authenticated, can't retrieve authority value");
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(LaBoulangerieCore.PLUGIN, "authority"), PersistentDataType.STRING);
    }

    public UUID getAuthorityUUID() {
        return UUID.fromString(getValue().substring(1));
    }
}
