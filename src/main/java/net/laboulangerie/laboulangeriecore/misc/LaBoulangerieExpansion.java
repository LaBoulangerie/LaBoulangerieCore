package net.laboulangerie.laboulangeriecore.misc;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * LaBoulangerieExpansion
 */
public class LaBoulangerieExpansion extends PlaceholderExpansion {
    @Override
    public String getAuthor() { return "La Boulangerie"; }
    @Override
    public String getIdentifier() { return "lbcore"; }
    @Override
    public String getVersion() { return "1.0.0"; }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("key_")) {
            String name = params.split("_")[1];
            char key = ' ';
            switch (name) {
                case "z":
                case "q":
                case "s":
                case "d":
                    key = name.charAt(0);
                    break;
                case "left":
                    key = '#';
                    break;
                case "right":
                    key = '"';
                    break;
                case "shift":
                    key = '!';
                    break;
                case "wheel":
                    key = '$';
                    break;
                case "arrow_up":
                    key = '0';
                    break;
                case "arrow_left":
                    key = '1';
                    break;
                case "arrow_down":
                    key = '2';
                    break;
                case "arrow_right":
                    key = '3';
                    break;
                default:
                    return null;
            }
            return "<font:bread_dough:controls>"+key+"</font>";
        }
        return null;
    }
}
