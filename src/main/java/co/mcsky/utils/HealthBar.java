package co.mcsky.utils;

import co.mcsky.MoeUtils;
import co.mcsky.TagHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthBar {

    private final TagHandler th;
    private final MoeUtils moe;

    public HealthBar(TagHandler th, MoeUtils moe) {
        this.th = th;
        this.moe = moe;
    }

    public void showHealth(Player player) {
        // Use the already-existing scoreboard of player instead of creating new one
        Scoreboard scoreboard = player.getScoreboard();
        try {
            String name = "showhealth";
            String criteria = "health";
            String text = th.color("/ " + (int) Math.ceil(player.getHealth()));
            Objective obj = scoreboard.registerNewObjective(name, criteria, text);
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
