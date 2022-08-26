package net.laboulangerie.laboulangeriecore.advancements;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreBoardManager {
    public static void setPlayerScore(OfflinePlayer player) {
        Objective objective;
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getMainScoreboard();
        if (board.getObjective("ability_used_count") != null) {
            objective = board.getObjective("ability_used_count");
        } else {
            objective = board.registerNewObjective("ability_used_count", "dummy");
        }
        useObjectives(objective, player, board);
    }

    public static void useObjectives(Objective objective, OfflinePlayer player, Scoreboard board) {
        player.getPlayer().setScoreboard(board);
        Score score = objective.getScore(player);
        score.setScore(score.getScore()+1);
        if (score.getScore() >= 100) {
            AdvancementManager.tryToCompleteAdvancement(player.getPlayer(), "mmo/farmer/grow_100_potatoes");
        }
    }
}