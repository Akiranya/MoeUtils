package co.mcsky.utilities;

import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardUtil {

    private final NametagUtil tag;
    private final MoeUtils moe;

    public ScoreboardUtil(NametagUtil tag, MoeUtils moe) {
        this.tag = tag;
        this.moe = moe;
    }

    public void showHealth(Player player) {
        // Use the already-existing scoreboard of player instead of creating new one
        Scoreboard scoreboard = player.getScoreboard();
        final String name = "showhealth";
        final String criteria = "health";
        final String displayName = tag.color("/ " + (int) Math.ceil(player.getHealth()));
        try {
            Objective obj = scoreboard.registerNewObjective(name, criteria, displayName);
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            player.setScoreboard(scoreboard);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Suppress exception "An objective of name 'showhealth' already exists"
        }
        Bukkit.getScheduler().runTaskLater(moe, () -> player.setHealth(player.getHealth()), 20);
    }

    public void removeHealth(Player player) {
        try {
            // Reset player's scoreboard by setting back to the main scoreboard
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // Happens if the player is logging out, just swallow it
        }
    }

}
