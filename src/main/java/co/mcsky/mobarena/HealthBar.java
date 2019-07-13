package co.mcsky.mobarena;

import co.mcsky.CustomPlayerName;
import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthBar {

    private CustomPlayerName custom;
    private MoeUtils plugin;

    public HealthBar(CustomPlayerName custom, MoeUtils plugin) {
        this.custom = custom;
        this.plugin = plugin;
    }

    void showHealth(Player player) {
        // Use the already-existing scoreboard of player instead of creating new one
        Scoreboard scoreboard = player.getScoreboard();
        try {
            String name = "showhealth";
            String criteria = "health";
            String text = custom.color("/ " + (int) Math.ceil(player.getHealth()));
            Objective obj = scoreboard.registerNewObjective(name, criteria, text);
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            player.setScoreboard(scoreboard);
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setHealth(player.getHealth()), 20);
    }

    void removeHealth(Player player) {
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
