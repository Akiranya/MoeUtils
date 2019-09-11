package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public void setWeather(Player player, Weather weather, int cost) {
        String worldName = player.getWorld().getName();
        String worldKey = asWorldKey(worldName);
        if (checkCooldown(player, worldKey) || checkBalance(player, cost)) return;
        weather.setWeather(moe, player); // 改变当前世界的天气
        String weatherName = weather.getDisplayName(moe);
        String weatherBroadcastMsg = String.format(moe.setting.magic_weather.msg_changed, worldName, weatherName);
        moe.getServer().broadcastMessage(weatherBroadcastMsg); // 全服播报
        charge(player, cost); // 扣费
        lastUsedWorld.put(worldName, player.getUniqueId());
        use(worldKey); // 更新冷却时间
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String msg = String.format(moe.setting.magic_weather.msg_finished, weatherName, worldName);
            moe.getServer().broadcastMessage(msg); // 当事件结束时播报一次
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
            String cooldownKey = asWorldKey(worldName);
            if (!check(cooldownKey, COOLDOWN_LENGTH)) { // If cooldown is not ready yet
                String statusMsg = String.format(moe.setting.magic_weather.msg_status,
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

    private String asWorldKey(String worldName) {
        return COOLDOWN_KEY + worldName;
    }

    public enum Weather {
        RAIN("storm"),
        CLEAR("sun"),
        THUNDER("thunder");

        private final String cmdArg;

        Weather(String cmdArg) {
            this.cmdArg = cmdArg;
        }

        public String getCmdArg() {
            return cmdArg;
        }

        public String getDisplayName(MoeUtils moe) {
            switch (this) {
                case CLEAR:
                    return moe.setting.magic_weather.msg_clear;
                case RAIN:
                    return moe.setting.magic_weather.msg_rain;
                case THUNDER:
                    return moe.setting.magic_weather.msg_thunder;
                default:
                    throw new IllegalStateException("Unknown weather value.");
            }
        }

        /**
         * Depending on the enum value, changes the weather for given world where player runs the command.
         *
         * @param player Who changes the weather.
         */
        public void setWeather(MoeUtils moe, Player player) {
            CommandSender console = moe.getServer().getConsoleSender();
            String world = player.getWorld().getName();
            int duration = 3600; // In second
            switch (this) {
                case CLEAR:
                case RAIN:
                    String msg = String.format("essentials:weather %s %s %d", world, getCmdArg(), duration);
                    moe.getServer().dispatchCommand(console, msg);
                    break;
                case THUNDER:
                    player.getWorld().setStorm(true);
                    player.getWorld().setThundering(true);
                    player.getWorld().setThunderDuration(TimeUtil.toTick(duration));
                    break;
                default:
                    throw new IllegalStateException("Unknown weather value.");
            }
        }
    }

}
