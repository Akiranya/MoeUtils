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
    private static final String COOLDOWN_KEY = "magicweather";
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
        // 如果玩家冷却还为就绪，直接 return
        String worldName = player.getWorld().getName();
        String cooldownKey = COOLDOWN_KEY + worldName;
        if (!checkCooldown(player, cooldownKey)) return;

        // 如果玩家没有足够的钱，直接 return
        if (!checkBalance(player, cost)) return;

        // Set weather
        weatherType.setWeather(moe, player);
        // Broadcast the event
        String weatherName = weatherType.getName(moe);
        String weatherBroadcastMsg = String.format(
                moe.config.magicweather_message_changed,
                worldName,
                weatherName);
        moe.getServer().broadcastMessage(weatherBroadcastMsg);

        // Charge player
        chargePlayer(player, cost);

        try {
            lastUsedWorld.put(worldName, player.getUniqueId()); // After all operations, don't forget to put it into map!
            Cooldown.getInstance().use(cooldownKey); // Call Cooldown.use() to update lastUsedTime
        } catch (NullPointerException e) { // Just in case ...
            moe.getLogger().warning(e.getMessage());
            return;
        }

        // Broadcast the end of event after given duration
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String msg = String.format(
                    moe.config.magicweather_message_ended,
                    weatherName,
                    worldName);
            moe.getServer().broadcastMessage(msg);
        }, toTick(cooldown));
    }

    /**
     * List all worlds whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        // Simply return (show nothing to player)
        // if there is no world in cooldown.
        if (lastUsedWorld.isEmpty()) return;

        // If players are not in map lastUsedWorld,
        // they won't be shown in the output of getStatus()
        lastUsedWorld.forEach((worldName, playersUUID) -> {
            String cooldownKey = COOLDOWN_KEY + worldName;

            if (!Cooldown.getInstance().check(cooldownKey, cooldown)) { // If cooldown is not ready yet
                String statusMsg = String.format(
                        moe.config.magicweather_message_status,
                        worldName,
                        moe.config.global_message_on,
                        moe.getServer().getOfflinePlayer(playersUUID).getName(),
                        Cooldown.getInstance().remaining(cooldownKey, cooldown));
                player.sendMessage(statusMsg);
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

    private void chargePlayer(Player player, int cost) {
        String commandFormat = "hamsterecohelper:heh balance take %s %d";
        String commandCharge = String.format(
                commandFormat,
                player.getName(),
                cost);
        moe.getServer().dispatchCommand(moe.getServer().getConsoleSender(), commandCharge); // This command CHARGES player
        player.sendMessage(String.format(moe.config.magicweather_message_cost, cost));
    }

    /**
     * @param player Player
     * @param cost   Cost
     * @return 玩家有足够的钱则返回 True 反之 False
     */
    private boolean checkBalance(Player player, int cost) {
        if (economy.has(player, cost)) {
            return true;
        }
        player.sendMessage(moe.config.global_message_notenoughmoney);
        return false;
    }

    private boolean checkCooldown(Player player, String cooldownKey) {
        if (Cooldown.getInstance().check(cooldownKey, cooldown)) {
            return true;
        }

        String playerMsg = String.format(
                moe.config.global_message_cooldown,
                Cooldown.getInstance().remaining(cooldownKey, cooldown));
        player.sendMessage(playerMsg);
        return false;
    }
}
