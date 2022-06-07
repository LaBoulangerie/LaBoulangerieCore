package net.laboulangerie.laboulangeriecore.advancements;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.palmergames.bukkit.towny.TownyAPI;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.events.MmoPlayerUseAbilityEvent;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilitiesManager;

public class AdvancementListeners implements Listener {

    AbilitiesManager abilitiesManager = new AbilitiesManager();
    public HashMap<String, Long> cooldowns_firebow = new HashMap<String, Long>();
    public HashMap<String, Long> cooldowns_dodge = new HashMap<String, Long>();

    @EventHandler
    public void onPlayerUseAbility(MmoPlayerUseAbilityEvent event) {
        Player player = Bukkit.getPlayer(event.getMmoPlayer().getUniqueId());
        switch (event.getAbility()) {
            case ANIMAL_TWINS:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/jumeaux");
                break;
            case BETTER_APPLE_DROP:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/chance_du_bucheron");
                break;
            case BETTER_BONEMEAL:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/engrais_naturel");
                break;
            case DODGING:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/esquive");
                cooldowns_dodge.put(player.getUniqueId().toString(), System.currentTimeMillis() + (8*1000));
                break;
            case DOUBLE_DROP_LOG:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/chance_du_bucheron");
                break;
            case EXP_IN_BOTTLE:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/mise_en_bouteille");
                break;
            case FAST_MINE:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/pioche_aiguisee");
                break;
            case FAST_SMELT:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/cuisson_instantanee");
                break;
            case FIRE_BOW:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/fleche_enflammee");
                cooldowns_firebow.put(player.getUniqueId().toString(), System.currentTimeMillis() + (8*1000));
                break;
            case HIDING:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/camouflage");
                break;
            case MAGNETIC_FIELD:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/champ_magnetique");
                break;
            case MINECRAFT_EXP_MULTIPLIER:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/brossage_minutieux");
                break;
            case NATURE_TOUCH:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/jeune_pousse");
                break;
            case STRIP:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/strip");
                break;
            case TASTY_BREAD:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/pain_odorant");
                break;
            case THICK_TREE:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/haricot_magique");
                ComboCompletedEvent event2 = (ComboCompletedEvent )event.getTriggerEvent();
                Block block = event2.getPlayer().getTargetBlock(5);
                for (Entity entity : block.getLocation().getNearbyEntities(1, 5, 1)) {
                    if(entity instanceof Player) {
                        AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/tree_in_player");
                    }
                }
                break;
            case TIMBER:
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/timber");
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerKillPlayer(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            //Player killer = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if(event.getFinalDamage() > victim.getHealth()){
                if (event.getDamager() instanceof Player) {
                    Player killer = (Player) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(killer);
                    if (killer.getInventory().getItemInMainHand().getType() == Material.BREAD && mmoPlayer.canUseAbility(Abilities.TASTY_BREAD)){
                        AdvancementManager.tryToCompleteAdvancement(killer, "mmo/farmer/kill_player_with_bread");
                    }
                    if (mmoPlayer.getTalent("thehunter").getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= Abilities.HIDING.getRequiredLevel()) {
                        if (killer.getActivePotionEffects().contains(killer.getPotionEffect(PotionEffectType.INVISIBILITY))) {
                            AdvancementManager.tryToCompleteAdvancement(killer, "mmo/hunter/kill_with_camouflage");
                        }
                    }
                }
                if(event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(victim);
                    if (arrow.getShooter() instanceof Player) {
                        Player shooter = (Player) arrow.getShooter();
                        if(cooldowns_firebow.containsKey(victim.getUniqueId().toString())) {
                            if (mmoPlayer.canUseAbility(Abilities.FIRE_BOW) && shooter.getName() == victim.getName() && cooldowns_firebow.get(victim.getUniqueId().toString()) > System.currentTimeMillis()) {
                                AdvancementManager.tryToCompleteAdvancement(victim, "mmo/hunter/kill_yourself_with_explosive_arrow");
                            }
                        }
                        if(cooldowns_firebow.containsKey(shooter.getUniqueId().toString())) {
                            if (mmoPlayer.canUseAbility(Abilities.FIRE_BOW) && shooter.getName() != victim.getName() && cooldowns_firebow.get(shooter.getUniqueId().toString()) > System.currentTimeMillis()) {
                                AdvancementManager.tryToCompleteAdvancement(shooter, "mmo/hunter/terrorism");
                            }
                        }
                    }
                }
                if(event.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) event.getDamager();
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(victim);
                    if (tnt.getSource() instanceof Player) {
                        Player summoner = (Player) tnt.getSource();
                        if(cooldowns_firebow.containsKey(victim.getUniqueId().toString())) {
                            if (mmoPlayer.canUseAbility(Abilities.FIRE_BOW) && summoner.getName() == victim.getName() && cooldowns_firebow.get(victim.getUniqueId().toString()) > System.currentTimeMillis()) {
                                AdvancementManager.tryToCompleteAdvancement(victim, "mmo/hunter/kill_yourself_with_explosive_arrow");
                            }
                        }
                        if(cooldowns_firebow.containsKey(summoner.getUniqueId().toString())) {
                            if (mmoPlayer.canUseAbility(Abilities.FIRE_BOW) && summoner.getName() != victim.getName() && cooldowns_firebow.get(summoner.getUniqueId().toString()) > System.currentTimeMillis()) {
                                AdvancementManager.tryToCompleteAdvancement(summoner, "mmo/hunter/terrorism");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (event.getRightClicked() instanceof Player  && player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE && mmoPlayer.canUseAbility(Abilities.STRIP) && AdvancementManager.playerHasAdvancement(player, "mmo/farmer/strip_a_player")) {
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/lumberjack/strip_a_player");
            AdvancementManager.tryToCompleteAdvancement((Player) event.getRightClicked(), "mmo/lumberjack/strip_a_player");
            mmoPlayer.useAbility(Abilities.STRIP);
            player.sendMessage("Vous venez de Strip " + event.getRightClicked().getName());
            event.getRightClicked().sendMessage("Vous venez d'être strip par " + player.getName());
        }
        if (event.getRightClicked() instanceof Player  && player.getInventory().getItemInMainHand().getType() == Material.BONE_MEAL && mmoPlayer.canUseAbility(Abilities.BETTER_BONEMEAL)  && AdvancementManager.playerHasAdvancement(player, "mmo/farmer/engrais_a_player")) {
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/engrais_a_player");
            AdvancementManager.tryToCompleteAdvancement((Player) event.getRightClicked(), "mmo/farmer/engrais_a_player");
            mmoPlayer.useAbility(Abilities.BETTER_BONEMEAL);
            event.getRightClicked().getLocation().getBlock().applyBoneMeal(BlockFace.DOWN);
            player.sendMessage("Il n'a pas l'air d'avoir poussé...");
            event.getRightClicked().sendMessage("Tu n'a pas l'air d'avoir poussé...");
        }
        if (event.getRightClicked() instanceof Player  && player.getInventory().getItemInMainHand().getType() == Material.WHEAT && mmoPlayer.canUseAbility(Abilities.ANIMAL_TWINS) && AdvancementManager.playerHasAdvancement(player, "mmo/farmer/jumeaux_a_player")) {
            event.getRightClicked().getWorld().spawnEntity(event.getRightClicked().getLocation(), EntityType.COW);
            player.sendMessage("Bizarre, son jumeaux ne lui ressemble pas");
            event.getRightClicked().sendMessage("Bizarre, ton jumeaux ne te ressemble pas");
            AdvancementManager.tryToCompleteAdvancement((Player) event.getRightClicked(), "mmo/farmer/jumeaux_a_player");
            AdvancementManager.tryToCompleteAdvancement(player, "mmo/farmer/jumeaux_a_player");
            mmoPlayer.useAbility(Abilities.ANIMAL_TWINS);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (player.getActivePotionEffects().contains(player.getPotionEffect(PotionEffectType.FAST_DIGGING))) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
            if(block.getType() == Material.OBSIDIAN && effect.getAmplifier() >= 2) {
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/pioche_aiguisee_on_obsidian");
            }
        }
        if (mmoPlayer.canUseAbility(Abilities.BETTER_BONEMEAL) && block.getType() == Material.POTATOES && !(AdvancementManager.playerHasAdvancement(player, "mmo/farmer/grow_100_potatoes"))) {
            ScoreBoardManager.setPlayerScore(player.getPlayer());
        }
        if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.EMERALD_ORE || block.getType() == Material.REDSTONE_ORE || block.getType() == Material.NETHER_QUARTZ_ORE){
            if (event.getExpToDrop()*5 >= 25) {
                AdvancementManager.tryToCompleteAdvancement(player, "mmo/miner/gain_25_exp_with_ore");
            }
        }
    }

    @EventHandler
    public void whenEntityShootsBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
            if(event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                if(cooldowns_firebow.containsKey(player.getUniqueId().toString())) {
                    if(TownyAPI.getInstance().getResident(player).getTownOrNull() != null) {
                        if (mmoPlayer.canUseAbility(Abilities.FIRE_BOW) && cooldowns_firebow.get(player.getUniqueId().toString()) > System.currentTimeMillis() && TownyAPI.getInstance().getTown(arrow.getLocation()) == TownyAPI.getInstance().getResident(player).getTownOrNull()) {
                            AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/fire_arrow_in_claim");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void whenPlayerDies(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if(event.getPlayer().getLastDamageCause().getCause() == DamageCause.FALL) {
            if(cooldowns_dodge.containsKey(player.getUniqueId().toString())) {
                if (mmoPlayer.canUseAbility(Abilities.DODGING) && cooldowns_dodge.get(player.getUniqueId().toString()) > System.currentTimeMillis()) {
                    AdvancementManager.tryToCompleteAdvancement(player, "mmo/hunter/dodge_life");
                }
            }
        }
    }
}
