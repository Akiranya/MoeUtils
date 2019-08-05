package co.mcsky.magicweather;

import co.mcsky.MoeUtils;
import co.mcsky.utils.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static co.mcsky.MoeUtils.economy;
import static co.mcsky.utils.MoeLib.toTick;

/**
 * Singleton class.
 */
public class MagicWeather {
    private static final String COOLDOWNKEY = "magicweather";
    private static MagicWeather magicWeather = null;
    private final MoeUtils moe;
    private final int cooldown; // Cooldown specified in plugin config

    /**
     * K = The name of world.<br>
     * V = Player who lastly used magic weather in given world.
     */
    private final Map<String, UUID> lastUsedWorld;

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
        // Check cooldown
        String worldName = player.getWorld().getName();
        String cooldownKey = COOLDOWNKEY + worldName;
        if (!Cooldown.getInstance().check(cooldownKey, cooldown)) { // If cooldown is not ready yet...
            String msg = moe.config.global_message_cooldown;
            int remaining = Cooldown.getInstance().remaining(cooldownKey, cooldown); // Note that cooldown is in second
            player.sendMessage(String.format(msg, remaining));
            return;
        }

        // Check balance
        if (!economy.has(player, cost)) { // Only if player has enough money do we put it into map
            player.sendMessage(moe.config.global_message_notenoughmoney);
            return;
        }

        // Set weather
        weatherType.setWeather(moe, player);
        // Broadcast the event
        String weatherName = weatherType.getName(moe);
        String msg = moe.config.magicweather_message_changed;
        moe.getServer().broadcastMessage(String.format(msg, worldName, weatherName));

        // Charge player
        String commandFormat = "hamsterecohelper:heh balance take %s %d";
        String commandCharge = String.format(commandFormat, player.getName(), cost);
        moe.getServer().dispatchCommand(moe.getServer().getConsoleSender(), commandCharge);
        player.sendMessage(String.format(moe.config.magicweather_message_cost, cost)); // This command CHARGES player

        try {
            // After all operations, dont forget to put it into map!
            lastUsedWorld.put(worldName, player.getUniqueId());
            // Call Cooldown.use() to update lastUsedTime
            Cooldown.getInstance().use(cooldownKey);
        } catch (NullPointerException e) { // Just in case
            moe.getLogger().warning(e.getMessage());
            return;
        }

        // Broadcast the end of event after given duration
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String format = String.format(moe.config.magicweather_message_ended, weatherName, worldName);
            moe.getServer().broadcastMessage(format);
        }, toTick(cooldown)).getTaskId();
    }

    /**
     * List all worlds whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        if (lastUsedWorld.isEmpty()) {
            return; // Simply return (show nothing to player) if there is no world in cooldown
        }
        // If players are not in map lastUsedWorld, they won't be shown in the output of getStatus()
        lastUsedWorld.forEach((worldName, playersUUID) -> {
            String cooldownKey = COOLDOWNKEY + worldName;
            if (!Cooldown.getInstance().check(cooldownKey, cooldown)) { // If cooldown is not ready yet
                String activated = moe.config.global_message_on;
                String lastUsedPlayer = moe.getServer().getOfflinePlayer(playersUUID).getName();
                int remaining = Cooldown.getInstance().remaining(cooldownKey, cooldown);
                String format = String.format(moe.config.magicweather_message_status, worldName, activated, lastUsedPlayer, remaining);
                player.sendMessage(format);
            }
        });
    }

    /**
     * Reset the cooldown in given world.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player, String world) {
        lastUsedWorld.remove(world);
        player.sendMessage(moe.config.magicweather_message_reset);
    }

}
