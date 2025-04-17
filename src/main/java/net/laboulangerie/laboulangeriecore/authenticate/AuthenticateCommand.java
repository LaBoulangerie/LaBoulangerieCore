package net.laboulangerie.laboulangeriecore.authenticate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.nation.Nation;
import me.angeschossen.lands.api.player.LandPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class AuthenticateCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4This command is restricted to players!");
            return true;
        }
        if ((args.length < 1 && !Arrays.asList("player","town", "nation").contains(args[0]))) return false;
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§4Vous devez avoir un objet en main !");
            return true;
        }

        Authenticable authenticable = new Authenticable(item);
        if (authenticable.isAuthenticated()) {
            player.sendMessage("§4Votre objet est déjà authentifié !");
            return true;
        }
        
        LandPlayer resident = LaBoulangerieCore.apiLands.getLandPlayer(player.getUniqueId());

        // Sign as a player, will be overwrote if nation or city parameter is provided
        String loreText = Authenticable.parseLore(
                player.displayName() != null ? PlainTextComponentSerializer.plainText().serialize(player.displayName())
                        : player.getName(),
                AuthorityType.PLAYER);

        // first letter correspond to the authority type, t = player, n = nation, t = town
        String authorityId = AuthorityType.PLAYER.getPrefix() + player.getUniqueId().toString();

        switch (args[0]) {
            case "town":
                Land town = resident.getLands().stream().findFirst().orElse(null);

                if(town == null){
                    player.sendMessage("§4Vous n'êtes dans aucune ville !");
                    return true;
                }

                if (!town.getDefaultArea().hasRoleFlag(resident.getUUID(), LaBoulangerieCore.townAuthenticateFlag)){
                    player.sendMessage("§4Pour authentifier un objet au nom de votre ville, vous devez en avoir la permission !");
                    return true;
                }
                loreText = Authenticable.parseLore(town.getName().replace('_', ' '), AuthorityType.TOWN);
                authorityId = AuthorityType.TOWN.getPrefix() + town.getULID().toString();
                break;
            case "nation":
                Nation nation = resident.getLands().stream().findFirst().orElse(null).getNation();

                if(nation == null){
                    player.sendMessage("§4Vous n'êtes dans aucune nation !");
                    return true;
                }

                if (!nation.getCapital().getDefaultArea().hasRoleFlag(resident.getUUID(), LaBoulangerieCore.nationAuthenticateFlag)) {
                    player.sendMessage("§4Pour authentifier un objet au nom de votre nation, vous devez en avoir la permission !");
                    return true;
                }
                loreText = Authenticable.parseLore(nation.getName().replace('_', ' '), AuthorityType.NATION);
                authorityId = AuthorityType.NATION.getPrefix() + nation.getULID().toString();
            case "player":
                loreText = Authenticable.parseLore(player.displayName().toString(), AuthorityType.PLAYER);
                authorityId = AuthorityType.PLAYER.getPrefix() + player.getUniqueId().toString();
            default:
                break;
        }

        List<Component> lore = Optional.ofNullable(item.lore()).orElse(new ArrayList<Component>());
        lore.add(Component.text(loreText));

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(LaBoulangerieCore.PLUGIN, "authority"),
                PersistentDataType.STRING, authorityId);
        item.setItemMeta(meta);

        item.lore(lore);

        try {
            YamlConfiguration data = UsersData.getOrCreate(player);
            data.set("authentications-count", data.getInt("authentications-count", 0) + 1);
            UsersData.save(player, data);
        } catch (IOException e) {
            LaBoulangerieCore.PLUGIN.getLogger()
                    .severe("Failed to increase authentication counter for player: " + player.getName());
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("player", "town", "nation");
        return Arrays.asList("");
    }
}
