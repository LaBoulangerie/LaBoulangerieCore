package net.laboulangerie.laboulangeriecore.eastereggs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;
import net.laboulangerie.laboulangeriecore.core.UsersData;

public class eEggHeadClick implements Listener {
    @EventHandler
    public void eastereggClick (PlayerInteractEvent e) throws IOException {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() == null || e.getHand() != EquipmentSlot.HAND) return;
        if(e.getClickedBlock().getType() == Material.PLAYER_HEAD || e.getClickedBlock().getType() == Material.PLAYER_WALL_HEAD){
            Player p = e.getPlayer();

            YamlConfiguration playerData = UsersData.get(p).orElseGet(() -> {
                try {
                    return UsersData.createUserData(p);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return null;
                }
            });
            Block block = e.getClickedBlock();

            String result = eEggUtil.getBlockIdentifier(block);
            List<String> foundEggs = new ArrayList<>();

            if(eEggUtil.eggsData.getStringList("eggs").contains(result)) {
                if(playerData.getStringList("eggs") != null || !playerData.getStringList("eggs").isEmpty()){

                    foundEggs = playerData.getStringList("eggs");
                    if (foundEggs.contains(result)) {
                        eEggUtil.sendAlreadyValidated(p);
                        return;
                    }
                }
                foundEggs.add(result);
                playerData.set("eggs", foundEggs);
                UsersData.save(p, playerData);
                eEggUtil.giveGift(p);
                eEggUtil.sendValidation(p);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) { // Add number of eggs found to the infos sent by the cmd /stats of LaBoulangerieMmo
        if (!event.getMessage().equals("/stats")) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().sendMessage(LaBoulangerieCore.PLUGIN.getConfig().getString("eastereggs.messages.eggs-found").replace("%amount%", eEggUtil.getPlayerAmount(event.getPlayer()).toString()).replace("%total%", eEggUtil.getMaxAmount().toString()));
            }
        }.runTaskAsynchronously(LaBoulangerieCore.PLUGIN); // Run asynchronously to add the text at the end
    }
}
