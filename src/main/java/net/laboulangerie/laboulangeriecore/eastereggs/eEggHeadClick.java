package net.laboulangerie.laboulangeriecore.eastereggs;

import java.io.File;
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

public class eEggHeadClick implements Listener {
    @EventHandler
    public void eastereggClick (PlayerInteractEvent e) throws IOException {

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() == null) return;
        if(e.getClickedBlock().getType() == Material.PLAYER_HEAD || e.getClickedBlock().getType() == Material.PLAYER_WALL_HEAD){
            Player p = e.getPlayer();
            eEggFileUtil.fileExist(p);

            File file = eEggFileUtil.getPlayerFile(p);
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(file);
            Block block = e.getClickedBlock();

            String result = eEggUtil.getBlockIdentifier(block);
            List<String> foundEggs = new ArrayList<>();

            if(eEggFileUtil.eggsData.getStringList("eggs").contains(result)) {
                if(playerData.getStringList("eggs") != null || !playerData.getStringList("eggs").isEmpty()){

                    foundEggs = playerData.getStringList("eggs");
                    if(e.getHand() == EquipmentSlot.HAND) {
                        if (foundEggs.contains(result)) {
                            eEggUtil.sendAlreadyValidated(p);
                            return;
                        }
                        if (!foundEggs.contains(result)) {
                            foundEggs.add(eEggUtil.getBlockIdentifier(block));
                            playerData.set("eggs", foundEggs);
                            playerData.save(file);
                            eEggUtil.giveGift(p);
                            eEggUtil.sendValidation(p);
                            return;
                        }
                    }
                }else {
                    eEggUtil.sendValidation(p);
                    foundEggs.add(result);
                    playerData.set("eggs", foundEggs);
                    playerData.save(file);
                    eEggUtil.giveGift(p);
                    eEggUtil.sendValidation(p);
                    return;
                }
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
