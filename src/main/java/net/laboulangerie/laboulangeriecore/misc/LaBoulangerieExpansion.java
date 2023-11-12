package net.laboulangerie.laboulangeriecore.misc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.laboulangerie.laboulangeriecore.core.GaiartosDate;
import net.laboulangerie.laboulangeriecore.core.UsersData;

/**
 * LaBoulangerieExpansion
 */
public class LaBoulangerieExpansion extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "La Boulangerie";
    }

    @Override
    public String getIdentifier() {
        return "lbcore";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

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
            return "<font:bread_dough:controls>" + key + "</font>";
        }

        if (params.startsWith("date_")) {
            String option = params.split("_")[1];
            LocalDate now = LocalDate.now();
            GaiartosDate gNow = new GaiartosDate(now);

            switch (option) {
                case "month":
                    return gNow.getMonth();
                case "year":
                    return ((Integer) gNow.getYear()).toString();
                case "full":
                    return String.format("%d %s An %d", now.getDayOfMonth(), gNow.getMonth(), gNow.getYear());
                default:
                    return null;
            }
        }

        if (params.startsWith("eastereggs")) {
            Optional<YamlConfiguration> data = UsersData.get(player);
            if (data.isEmpty())
                return null;

            if (params.endsWith("worldguard")) {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
                if (!set.testState(WorldGuardPlugin.inst().wrapPlayer(player), Flags.INVINCIBILITY))
                    return "";
            }

            int count = data.get().getList("eggs", new ArrayList<>()).size();
            return count + "%img_easter_egg%";
        }
        return null;
    }
}
