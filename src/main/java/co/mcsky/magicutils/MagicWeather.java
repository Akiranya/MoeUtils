package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static co.mcsky.utils.MoeLib.toTick;

/**
 * Singleton class.
 */
public class MagicWeather extends AMagicUtils {
    private final static String COOLDOWN_KEY = "mw";
    private static MagicWeather magicWeather = null;

    // K = world name
    // V = player UUID
    private final Map<String, UUID> lastUsedWorld;

    private MagicWeather(MoeUtils moe) {
        super(moe, moe.config.magicweather_cooldown);
        lastUsedWorld = new HashMap<>();
    }

    public static MagicWeather getInstance(MoeUtils moe) {
        if (magicWeather == null) return magicWeather = new MagicWeather(moe);
        return magicWeather;
    }

    public void setWeather(Player player, EWeather weather, int cost) {
        String worldName = player.getWorld().getName();
        String worldKey = COOLDOWN_KEY + worldName;

        if (checkCooldown(player, worldKey)) return; // 如果冷却为就绪，直接 return
        if (checkBalance(player, cost)) return; // 如果玩家钱不够，直接 return

        weather.setWeather(moe, player); // 改变当前世界的天气
        String weatherName = weather.getName(moe);
        String weatherBroadcastMsg = String.format(
                moe.config.magicweather_message_changed,
                worldName,
                weatherName);
        moe.getServer().broadcastMessage(weatherBroadcastMsg); // 然后全服播报
        charge(player, cost); // 向玩家扣费

        lastUsedWorld.put(worldName, player.getUniqueId()); // Don't forget to put it in map!
        use(worldKey); // Updates last-used time for the world

        // 当事件结束时播报一次
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String msg = String.format(
                    moe.config.magicweather_message_ended,
                    weatherName,
                    worldName);
            moe.getServer().broadcastMessage(msg);
        }, toTick(COOLDOWN_LENGTH));
    }

    /**
     * List all worlds whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        // Simply return (show nothing to player) if there is no world in COOLDOWN_LENGTH.
        if (lastUsedWorld.isEmpty()) return;

        // If players are not in map lastUsedWorld,
        // they won't be shown in the output of getStatus()
        lastUsedWorld.forEach((worldName, playerUUID) -> {
            String cooldownKey = COOLDOWN_KEY + worldName;

            if (!check(cooldownKey, COOLDOWN_LENGTH)) { // If COOLDOWN_LENGTH is not ready yet
                String statusMsg = String.format(
                        moe.config.magicweather_message_status,
                        worldName,
                        moe.config.global_message_on,
                        moe.getServer().getOfflinePlayer(playerUUID).getName(),
                        remaining(cooldownKey, COOLDOWN_LENGTH));
                player.sendMessage(statusMsg);
            }
        });
    }

    public void reset(Player player) {
        lastUsedWorld.clear();
        String playerMsg = String.format(moe.config.global_message_reset, moe.config.magicweather_message_prefix);
        player.sendMessage(playerMsg);
    }

}
