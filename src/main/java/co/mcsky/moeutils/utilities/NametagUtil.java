package co.mcsky.moeutils.utilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NametagUtil {

    private final Map<Player, Team> teams;

    public NametagUtil() {
        teams = new HashMap<>();
    }

    /**
     * @param player The player whose prefix / suffix is gonna be changed
     * @param prefix The prefix to be added
     * @param suffix The suffix to be added
     * @param action Update or remove
     */
    public void change(Player player, String prefix, String suffix,
                       ACTION action) {
        if (player == null || prefix == null || suffix == null || action == null) {
            throw new IllegalArgumentException("Method arguments cannot be null.");
        }
        Scoreboard scoreboard = player.getScoreboard();
        Team team;
        switch (action) {
            case UPDATE:
                if (teams.containsKey(player)) {
                    team = teams.get(player);
                } else {
                    team = scoreboard.registerNewTeam(player.getName());
                }
                try {
                    team.setPrefix(color(prefix));
                    team.setSuffix(color(suffix));
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                    team.addEntry(player.getName());
                    teams.put(player, team);
                } catch (IllegalStateException | IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REMOVE:
                try {
                    teams.remove(player).unregister();
                } catch (IllegalStateException | NullPointerException ignored) {

                }
                break;
            default:
                throw new IllegalArgumentException("Unknown ACTION.");
        }
    }

    /**
     * Get players who have th prefix or suffix.
     *
     * @return Players who have th prefix or suffix.
     */
    public Set<Player> getPlayers() {
        return new HashSet<>(teams.keySet());
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public enum ACTION {
        UPDATE,
        REMOVE
    }

}

