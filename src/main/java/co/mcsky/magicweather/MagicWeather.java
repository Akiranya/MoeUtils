package co.mcsky.magicweather;

import co.mcsky.MoeUtils;
import co.mcsky.utils.Cooldown;
import co.mcsky.utils.MoeLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static co.mcsky.MoeUtils.economy;

/**
 * Singleton class.
 */
public class MagicWeather {
    private static MagicWeather magicWeather = null;
    /**
     * In second.
     */
    final private int cooldown;
    final private MoeUtils pl;
    /**
     * K = The name of world.<br>
     * V = Status of the world.
     */
    private Map<String, Status> lastUsedMap;
    private int broadcastTask;

    /**
     * Outer world has no access to the constructor.
     */
    private MagicWeather(MoeUtils pl) {
        this.pl = pl;
        cooldown = pl.getMoeConfig().magicweather_cooldown; // In second
        lastUsedMap = new HashMap<>();
    }

    public static MagicWeather getInstance(MoeUtils pl) {
        if (magicWeather != null) {
            return magicWeather;
        }
        return magicWeather = new MagicWeather(pl);
    }

    public void setWeather(Player player, WeatherType weatherType) {
        /* Check cooldown */
        final String worldName = player.getWorld().getName();
        Status status = lastUsedMap.get(worldName);
        // If there is no record in map, simply set lastUsedTime to 0
        final long lastUsedTime = status != null ? status.lastUsedTime : 0;
        final long now = System.currentTimeMillis(); // In millisecond
        Cooldown cd = MoeLib.cooldown(now, lastUsedTime, cooldown);
        if (!cd.isReady()) { // Note that cooldown is in second
            // If cooldown is not ready yet...
            player.sendMessage(
                    String.format(pl.getMoeConfig().global_message_cooldown, cd.getRemaining())
            );
            return;
        }

        /* Check balance */
        int cost = pl.getMoeConfig().magicweather_cost;
        if (!economy.has(player, cost)) {
            // Only if player has enough money do we put it into map
            player.sendMessage(pl.getMoeConfig().global_message_notenoughmoney);
            return;
        }

        /* Set weather */
        weatherType.setWeather(pl, player);
        // After setting weather, we broadcast it!
        pl.getServer().broadcastMessage(
                String.format(pl.getMoeConfig().magicweather_message_changed,
                        player.getWorld().getName(), weatherType.getName(pl))
        );

        /* Charge player */
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), commandCharge);
        player.sendMessage(String.format(pl.getMoeConfig().magicweather_message_cost, cost));

        // After all operations, dont forget to put it into map!
        lastUsedMap.put(worldName, new Status(now, player.getUniqueId()));

        /* Wait and broadcast */
        final String weatherName = weatherType.getName(pl);
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(
                pl, () -> pl.getServer().broadcastMessage(
                        String.format(pl.getMoeConfig().magicweather_message_ended,
                                weatherName, worldName)
                ), MoeLib.toTick(cooldown)).getTaskId();
    }

    /**
     * List all worlds whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        if (lastUsedMap.isEmpty()) {
            String none = pl.getMoeConfig().magicweather_message_none;
            player.sendMessage(
                    String.format(pl.getMoeConfig().magicweather_message_status,
                            none, none, none, 0)
            );
            return;
        }
        lastUsedMap.forEach((worldName, status) -> {
            long now = System.currentTimeMillis();
            long waited = TimeUnit.MILLISECONDS.toSeconds(now - status.lastUsedTime); // In second
            if (waited <= cooldown) {
                String activated = pl.getMoeConfig().global_message_on;
                String lastUsedPlayer = pl.getServer().getOfflinePlayer(status.lastUsedPlayer).getName();
                int remained = cooldown - (int) waited;
                player.sendMessage(
                        String.format(pl.getMoeConfig().magicweather_message_status,
                                worldName, activated, lastUsedPlayer, remained));
            } else {
                String activated = pl.getMoeConfig().global_message_off;
                player.sendMessage(String.format(pl.getMoeConfig().magicweather_message_status, activated, "", ""));
            }
        });
    }

    /**
     * This method simply removes the element from map to reset status for given world.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player, String world) {
        lastUsedMap.remove(world);
        player.sendMessage(pl.getMoeConfig().magicweather_message_reset);
    }

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }

    private class Status {
        /**
         * In millisecond.
         */
        long lastUsedTime;
        UUID lastUsedPlayer;

        Status(long lastUsedTime, UUID lastUsedPlayer) {
            this.lastUsedTime = lastUsedTime;
            this.lastUsedPlayer = lastUsedPlayer;
        }
    }
}
