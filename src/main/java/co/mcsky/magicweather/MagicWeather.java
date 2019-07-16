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
    final private MoeUtils moe;
    /**
     * K = The name of world.<br>
     * V = Status of the world.
     */
    private Map<String, Status> lastUsedMap;
    private int broadcastTask;

    /**
     * Outer world has no access to the constructor.
     */
    private MagicWeather(MoeUtils moe) {
        this.moe = moe;
        cooldown = moe.config.magicweather_cooldown; // In second
        lastUsedMap = new HashMap<>();
    }

    public static MagicWeather getInstance(MoeUtils moe) {
        if (magicWeather != null) {
            return magicWeather;
        }
        return magicWeather = new MagicWeather(moe);
    }

    public void setWeather(Player player, WeatherType weatherType) {
        /* Check cooldown */
        final String worldName = player.getWorld().getName();
        Status status = lastUsedMap.get(worldName);
        // If there is no record in map, simply set lastUsedTime to 0
        final long lastUsedTime = status != null ? status.lastUsedTime : 0;
        final long now = System.currentTimeMillis(); // In millisecond
        Cooldown cd = MoeLib.cooldown(now, lastUsedTime, cooldown);
        if (!cd.ready) { // Note that cooldown is in second
            // If cooldown is not ready yet...
            player.sendMessage(
                    String.format(moe.config.global_message_cooldown, cd.remaining)
            );
            return;
        }

        /* Check balance */
        int cost = moe.config.magicweather_cost;
        if (!economy.has(player, cost)) {
            // Only if player has enough money do we put it into map
            player.sendMessage(moe.config.global_message_notenoughmoney);
            return;
        }

        /* Set weather */
        weatherType.setWeather(moe, player);
        // After setting weather, we broadcast it!
        moe.getServer().broadcastMessage(
                String.format(moe.config.magicweather_message_changed,
                        player.getWorld().getName(), weatherType.getName(moe))
        );

        /* Charge player */
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        moe.getServer().dispatchCommand(moe.getServer().getConsoleSender(), commandCharge);
        player.sendMessage(String.format(moe.config.magicweather_message_cost, cost));

        // After all operations, dont forget to put it into map!
        lastUsedMap.put(worldName, new Status(now, player.getUniqueId()));

        /* Wait and broadcast */
        final String weatherName = weatherType.getName(moe);
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(
                moe, () -> moe.getServer().broadcastMessage(
                        String.format(moe.config.magicweather_message_ended,
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
            String none = moe.config.magicweather_message_none;
            player.sendMessage(
                    String.format(moe.config.magicweather_message_status,
                            none, none, none, 0)
            );
            return;
        }
        lastUsedMap.forEach((worldName, status) -> {
            long now = System.currentTimeMillis();
            long waited = TimeUnit.MILLISECONDS.toSeconds(now - status.lastUsedTime); // In second
            if (waited <= cooldown) {
                String activated = moe.config.global_message_on;
                String lastUsedPlayer = moe.getServer().getOfflinePlayer(status.lastUsedPlayer).getName();
                int remained = cooldown - (int) waited;
                player.sendMessage(
                        String.format(moe.config.magicweather_message_status,
                                worldName, activated, lastUsedPlayer, remained));
            } else {
                String activated = moe.config.global_message_off;
                player.sendMessage(String.format(moe.config.magicweather_message_status, activated, "", ""));
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
        player.sendMessage(moe.config.magicweather_message_reset);
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
