package net.laboulangerie.laboulangeriecore.advancements;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;


public class AdvancementListeners implements Listener {

    public HashMap<UUID, Long> cooldowns_firebow = new HashMap<UUID, Long>();
    public HashMap<UUID, Long> cooldowns_dodge = new HashMap<UUID, Long>();


    @EventHandler
    public void onPlayerKillPlayer(EntityDamageByEntityEvent event) {
        /*if (event.getEntity() instanceof Player) {
            // Player killer = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (event.getFinalDamage() > victim.getHealth()) {
                if (event.getDamager() instanceof Player) {
                    Player killer = (Player) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(killer);
                    if (killer.getInventory().getItemInMainHand().getType() == Material.BREAD && mmoPlayer
                            .canUseAbility(LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes
                                    .get("tasty-bread"), "farmer")) {
                        AdvancementManager.tryToCompleteAdvancement(killer, "mmo/farmer/kill_player_with_bread");
                    }
                    if (mmoPlayer
                            .canUseAbility(LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes
                                    .get("hiding"), "hunter")
                            && killer.getActivePotionEffects()
                                    .contains(killer.getPotionEffect(PotionEffectType.INVISIBILITY))) {
                        AdvancementManager.tryToCompleteAdvancement(killer, "mmo/hunter/kill_with_camouflage");
                    }
                }
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(victim);
                    if (arrow.getShooter() instanceof Player) {
                        Player shooter = (Player) arrow.getShooter();
                        if (cooldowns_firebow.containsKey(victim.getUniqueId())
                                && doesPlayerHaveRequiredLevel(mmoPlayer,
                                        LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes
                                                .get("fire-bow"),
                                        "hunter")
                                && shooter.getName() == victim.getName()
                                && cooldowns_firebow.get(victim.getUniqueId()) > System.currentTimeMillis()) {
                            AdvancementManager.tryToCompleteAdvancement(victim,
                                    "mmo/hunter/kill_yourself_with_explosive_arrow");
                        }
                        if (cooldowns_firebow.containsKey(shooter.getUniqueId())
                                && doesPlayerHaveRequiredLevel(mmoPlayer,
                                        LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes
                                                .get("fire-bow"),
                                        "hunter")
                                && shooter.getName() != victim.getName()
                                && cooldowns_firebow.get(shooter.getUniqueId()) > System.currentTimeMillis()) {
                            AdvancementManager.tryToCompleteAdvancement(shooter, "mmo/hunter/terrorism");
                        }
                    }
                }
                if (event.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(victim);
                    if (tnt.getSource() instanceof Player) {
                        Player summoner = (Player) tnt.getSource();
                        if (cooldowns_firebow.containsKey(victim.getUniqueId())
                                && doesPlayerHaveRequiredLevel(mmoPlayer,
                                        LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes
                                                .get("fire-bow"),
                                        "hunter")
                                && summoner.getName() == victim.getName()
                                && cooldowns_firebow.get(victim.getUniqueId()) > System.currentTimeMillis()) {
                            AdvancementManager.tryToCompleteAdvancement(victim,
                                    "mmo/hunter/kill_yourself_with_explosive_arrow");
                        }
                        if (cooldowns_firebow.containsKey(summoner.getUniqueId())
                                && doesPlayerHaveRequiredLevel(mmoPlayer,
                                        LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes
                                                .get("fire-bow"),
                                        "hunter")
                                && summoner.getName() != victim.getName()
                                && cooldowns_firebow.get(summoner.getUniqueId()) > System.currentTimeMillis()) {
                            AdvancementManager.tryToCompleteAdvancement(summoner, "mmo/hunter/terrorism");
                        }
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        /*Player player = event.getPlayer();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (event.getRightClicked() instanceof Player
                && player.getInventory().getItemInMainHand().getType() == Material.BONE_MEAL
                && doesPlayerHaveRequiredLevel(mmoPlayer,
                        LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes.get("better-bonemeal"),
                        "farmer")
                && !(AdvancementManager.playerHasAdvancement(player, "mmo/farmer/engrais_a_player"))) {
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/engrais_a_player");
            mmoPlayer.useAbility(
                    LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes.get("better-bonemeal"),
                    LaBoulangerieMmo.talentsRegistry.getTalent("farmer"));
            event.getRightClicked().getLocation().getBlock().applyBoneMeal(BlockFace.DOWN);
            player.sendMessage("Il n'a pas l'air d'avoir poussé...");
            event.getRightClicked().sendMessage("Tu n'a pas l'air d'avoir poussé...");
        }
        if (event.getRightClicked() instanceof Player
                && player.getInventory().getItemInMainHand().getType() == Material.WHEAT
                && doesPlayerHaveRequiredLevel(mmoPlayer,
                        LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes.get("animal-twins"),
                        "farmer")
                && !(AdvancementManager.playerHasAdvancement(player, "mmo/farmer/jumeaux_a_player"))) {
            event.getRightClicked().getWorld().spawnEntity(event.getRightClicked().getLocation(), EntityType.COW);
            player.sendMessage("Bizarre, son jumeaux ne lui ressemble pas");
            event.getRightClicked().sendMessage("Bizarre, ton jumeaux ne te ressemble pas");
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/jumeaux_a_player");
            mmoPlayer.useAbility(
                    LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes.get("better-bonemeal"),
                    LaBoulangerieMmo.talentsRegistry.getTalent("farmer"));
        }*/
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        /*Player player = event.getPlayer();
        Block block = event.getBlock();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (player.getActivePotionEffects().contains(player.getPotionEffect(PotionEffectType.HASTE))) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.HASTE);
            if (block.getType() == Material.OBSIDIAN && effect.getAmplifier() >= 2) {
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/pioche_aiguisee_on_obsidian");
            }
        }
        if (doesPlayerHaveRequiredLevel(mmoPlayer,
                LaBoulangerieMmo.talentsRegistry.getTalent("farmer").abilitiesArchetypes.get("better-bonemeal"),
                "farmer") && block.getType() == Material.POTATOES
                && !(AdvancementManager.playerHasAdvancement(player, "mmo/farmer/grow_100_potatoes"))) {
            ScoreBoardManager.setPlayerScore(player.getPlayer());
        }
        if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.EMERALD_ORE
                || block.getType() == Material.REDSTONE_ORE || block.getType() == Material.NETHER_QUARTZ_ORE) {
            if (event.getExpToDrop() * 5 >= 25) {
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/gain_25_exp_with_ore");
            }
        }*/
    }


    @EventHandler
    public void whenArrowIsShot(ProjectileHitEvent event) {
        /*if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
            Town town = TownyAPI.getInstance().getResident(player).getTownOrNull();
            if (event.getEntity() instanceof Arrow && cooldowns_firebow.containsKey(player.getUniqueId())
                    && town != null && event.getHitBlock() != null
                    && doesPlayerHaveRequiredLevel(mmoPlayer,
                            LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes.get("fire-bow"),
                            "hunter")
                    && TownyAPI.getInstance().getTown(event.getHitBlock().getLocation()) == town
                    && FireArrow.fireArrow.stream().filter(fireArrow -> fireArrow.getShooter() == player).findAny()
                            .isPresent() == true) {
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/fire_arrow_in_claim");
            }

        }*/
    }

    @EventHandler
    public void whenPlayerDies(PlayerDeathEvent event) {
        /*Player player = event.getPlayer();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (event.getPlayer().getLastDamageCause().getCause() == DamageCause.FALL
                && cooldowns_dodge.containsKey(player.getUniqueId())
                && doesPlayerHaveRequiredLevel(mmoPlayer,
                        LaBoulangerieMmo.talentsRegistry.getTalent("hunter").abilitiesArchetypes.get("dodging"),
                        "hunter")
                && cooldowns_dodge.get(player.getUniqueId()) > System.currentTimeMillis()) {
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/dodge_life");
        }*/
    }
}
