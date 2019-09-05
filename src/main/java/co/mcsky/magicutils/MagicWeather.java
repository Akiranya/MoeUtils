package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton class.
 */
public class MagicWeather extends AMagicUtils<String> {
    private final static String COOLDOWN_KEY = "magic_weather";
    private static MagicWeather magicWeather = null;

    // K = world name
    // V = player UUID
    private final Map<String, UUID> lastUsedWorld;

    private MagicWeather(MoeUtils moe) {
        super(moe, moe.setting.magic_weather.cooldown);
        lastUsedWorld = new HashMap<>();
    }

    public static MagicWeather getInstance(MoeUtils moe) {
        if (magicWeather == null) return magicWeather = new MagicWeather(moe);
        return magicWeather;
    }

    public void setWeather(Player player, EWeather weather, int cost) {
        String worldName = player.getWorld().getName();
        String worldKey = getWorldKey(worldName);

        if (checkCooldown(player, worldKey)) return; // 如果冷却为就绪，直接 return
        if (checkBalance(player, cost)) return; // 如果玩家钱不够，直接 return

        weather.setWeather(moe, player); // 改变当前世界的天气
        String weatherName = weather.getName(moe);
        String weatherBroadcastMsg = String.format(
                moe.setting.magic_weather.msg_changed,
                worldName,
                weatherName);
        moe.getServer().broadcastMessage(weatherBroadcastMsg); // 然后全服播报
        charge(player, cost); // 向玩家扣费

        lastUsedWorld.put(worldName, player.getUniqueId()); // Don't forget to put it in map!
        use(worldKey); // Updates last-used time for the world

        // 当事件结束时播报一次
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String msg = String.format(
                    moe.setting.magic_weather.msg_finished,
                    weatherName,
                    worldName);
            moe.getServer().broadcastMessage(msg);
        }, TimeUtil.toTick(COOLDOWN_LENGTH));
    }

    /**
     * List all enabled_world whose magic weather are on, and send these messages to given player.
     *
     * @param player Who receives the messages.
     */
    public void getStatus(Player player) {
        // Simply return (show nothing to player)
        // if there is no world in cooldown.
        if (lastUsedWorld.isEmpty()) return;

        // If players are not in map lastUsedWorld,
        // they won't be shown in the output of getStatus()
        lastUsedWorld.forEach((worldName, playerUUID) -> {
            String cooldownKey = getWorldKey(worldName);
            if (!check(cooldownKey, COOLDOWN_LENGTH)) { // If cooldown is not ready yet
                String statusMsg = String.format(
                        moe.setting.magic_weather.msg_status,
                        worldName,
                        moe.setting.globe.msg_on,
                        moe.getServer().getOfflinePlayer(playerUUID).getName(),
                        remaining(cooldownKey, COOLDOWN_LENGTH));
                player.sendMessage(statusMsg);
            }
        });
    }

    public void reset(Player player) {
        lastUsedWorld.clear();
        String playerMsg = String.format(moe.setting.globe.msg_reset, moe.setting.magic_weather.msg_prefix);
        player.sendMessage(playerMsg);
    }

    private String getWorldKey(String worldName) {
        return COOLDOWN_KEY + worldName;
    }

}
