package co.mcsky.magicweather;

import co.mcsky.MoeUtils;
import co.mcsky.utils.Cooldown;
import co.mcsky.utils.MoeLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static co.mcsky.MoeUtils.economy;

/**
 * Singleton class.
 */
public class MagicWeather {
    private MoeUtils moe;

    private static final String COOLDOWNKEY = "magicweather";

    private static MagicWeather magicWeather = null;

    /**
     * In second.
     */
    private int cooldown;


    /**
     * K = The name of world.<br>
     * V = Player who lastly used magic weather in given world.
     */
    private Map<String, UUID> lastUsedWorld;

    private int broadcastTask;

    /**
     * Outer world has no access to the constructor.
     */
    private MagicWeather(MoeUtils moe) {
        this.moe = moe;
        cooldown = moe.config.magicweather_cooldown; // In second
        lastUsedWorld = new HashMap<>();
    }

    public static MagicWeather getInstance(MoeUtils moe) {
        if (magicWeather != null) {
            return magicWeather;
        }
        return magicWeather = new MagicWeather(moe);
    }

    public void setWeather(Player player, WeatherType weatherType, int cost) {
        /* Check cooldown */
        String worldName = player.getWorld().getName();
        String key = COOLDOWNKEY + worldName;
        if (!Cooldown.getInstance().check(COOLDOWNKEY + worldName, cooldown)) {
            // Note that cooldown is in seconds
            // If cooldown is not ready yet...
            String msg = moe.config.global_message_cooldown;
            int remaining = Cooldown.getInstance().remaining(key, cooldown);
            player.sendMessage(String.format(msg, remaining));
            return;
        }

        /* Check balance */
        if (!economy.has(player, cost)) {
            // Only if player has enough money do we put it into map
            player.sendMessage(moe.config.global_message_notenoughmoney);
            return;
        }

        /* Set weather */
        weatherType.setWeather(moe, player);
        // After setting weather, we broadcast it!
        String weatherName = weatherType.getName(moe);
        String msg = moe.config.magicweather_message_changed;
        moe.getServer().broadcastMessage(String.format(msg, worldName, weatherName));

        /* Charge player */
        String commandFormat = "hamsterecohelper:heh balance take %s %d";
        String commandCharge = String.format(commandFormat, player.getName(), cost);
        moe.getServer().dispatchCommand(moe.getServer().getConsoleSender(), commandCharge);
        player.sendMessage(String.format(moe.config.magicweather_message_cost, cost));

        try {
            // After all operations, dont forget to put it into map!
            lastUsedWorld.put(worldName, player.getUniqueId());
            // Call Cooldown.use() to update lastUsedTime
            Cooldown.getInstance().use(key);
        } catch (NullPointerException e) {
            moe.getLogger().warning(e.getMessage());
            return;
        }

        /* Wait and broadcast */
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String format = String.format(moe.config.magicweather_message_ended, weatherName, worldName);
            moe.getServer().broadcastMessage(format);
        }, MoeLib.toTick(cooldown)).getTaskId();
    }

    /**
     * List all worlds whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        if (lastUsedWorld.isEmpty()) {
            // Simply return (show nothing to player) if there is no world in cooldown
            return;
        }
        // If players are not in map lastUsedWorld, they won't be shown in the output of getStatus()
        lastUsedWorld.forEach((worldName, playersUUID) -> {
            String key = COOLDOWNKEY + worldName;
            if (!Cooldown.getInstance().check(key, cooldown)) {
                // If cooldown is not ready yet
                String activated = moe.config.global_message_on;
                String lastUsedPlayer = moe.getServer().getOfflinePlayer(playersUUID).getName();
                int remained = Cooldown.getInstance().remaining(key, cooldown);
                player.sendMessage(String.format(moe.config.magicweather_message_status, worldName, activated, lastUsedPlayer, remained));
            }
        });
    }

    /**
     * This method simply removes the element from map to reset status for given world.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player, String world) {
        lastUsedWorld.remove(world);
        player.sendMessage(moe.config.magicweather_message_reset);
    }

    // TODO add reload method

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }
}
