package co.mcsky;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a singleton class, i.e. there is only one instance existing at the same time.<br>
 * All configurations should be listed here as members of this class.
 */
public class MoeConfig {

    private static MoeConfig moeConfig = null;
    private final MoeUtils moe;
    /* MobArena Addon */
    public boolean mobarena_on;
    public Set<EntityType> mobarena_whitelist;
    /* Safe Portal */
    public boolean safeportal_on;
    public boolean safeportal_debug_on;
    public EventPriority safeportal_priority;
    public String safeportal_message_player;
    public String safeportal_message_debug;
    /* MagicTime */
    public int magictime_cost;
    public int magictime_cooldown;
    public String magictime_message_day;
    public String magictime_message_night;
    public String magictime_message_prefix;
    public String magictime_message_reset;
    public String magictime_message_cost;
    public String magictime_message_status;
    public String magictime_message_ended;
    public String magictime_message_changed;
    /* MagicWeather */
    public int magicweather_cost;
    public int magicweather_cooldown;
    public String magicweather_message_prefix;
    public String magicweather_message_changed;
    public String magicweather_message_ended;
    public String magicweather_message_reset;
    public String magicweather_message_status;
    public String magicweather_message_cost;
    public String magicweather_message_rain;
    public String magicweather_message_clear;
    public String magicweather_message_thunder;
    public String magicweather_message_none;
    /* Global */
    public String global_message_reloaded;
    public String global_message_noperms;
    public String global_message_notenoughmoney;
    public String global_message_cooldown;
    public String global_message_on;
    public String global_message_off;
    public String global_message_playeronly;

    private MoeConfig(MoeUtils moe) {
        this.moe = moe;
        reloadConfig();
    }

    public static MoeConfig getInstance(MoeUtils moe) {
        if (moeConfig == null) {
            moeConfig = new MoeConfig(moe);
        }
        return moeConfig;
    }

    public void reloadConfig() {
        moe.reloadConfig();

        // Convenient local variable
        FileConfiguration config = moe.getConfig();

        // MobArena Addon initialization
        mobarena_on = config.getBoolean("mobarena-addon.enable");
        List<String> entityTypes = config.getStringList("mobarena-addon.whitelist");
        mobarena_whitelist = entityTypes.stream()
                .map(e -> EntityType.valueOf(e.toUpperCase()))
                .collect(Collectors.toSet());
        // Print out mobarena_whitelist for double-checking
        StringBuilder sb = new StringBuilder();
        mobarena_whitelist.forEach(e -> {
            sb.append(e);
            sb.append(" ");
        });
        moe.getLogger().info(sb.toString());

        // Safe Portal initialization
        safeportal_on = config.getBoolean("safe-portal.enable");
        safeportal_debug_on = config.getBoolean("safe-portal.debug");
        safeportal_priority = EventPriority.valueOf(config.getString("safe-portal.event-priority"));
        safeportal_message_player = color(config.getString("safe-portal.messages.cancelled"));
        safeportal_message_debug = color(config.getString("safe-portal.messages.debug"));

        /* MagicTime */
        magictime_cost = config.getInt("magictime.cost");
        magictime_cooldown = config.getInt("magictime.cooldown");
        magictime_message_prefix = color(config.getString("magictime.prefix"));
        magictime_message_cost = color(config.getString("magictime.messages.cost"));
        magictime_message_ended = color(config.getString("magictime.messages.ended"));
        magictime_message_changed = color(config.getString("magictime.messages.changed"));
        magictime_message_status = color(config.getString("magictime.messages.status"));
        magictime_message_reset = color(config.getString("magictime.messages.reset"));
        magictime_message_day = color(config.getString("magictime.messages.day"));
        magictime_message_night = color(config.getString("magictime.messages.night"));

        /* MagicWeather */
        magicweather_cost = config.getInt("magicweather.cost");
        magicweather_cooldown = config.getInt("magicweather.cooldown");
        magicweather_message_prefix = color(config.getString("magicweather.prefix"));
        magicweather_message_changed = color(config.getString("magicweather.messages.changed"));
        magicweather_message_ended = color(config.getString("magicweather.messages.ended"));
        magicweather_message_status = color(config.getString("magicweather.messages.status"));
        magicweather_message_cost = color(config.getString("magicweather.messages.cost"));
        magicweather_message_reset = color(config.getString("magicweather.messages.reset"));
        magicweather_message_rain = color(config.getString("magicweather.messages.rain"));
        magicweather_message_clear = color(config.getString("magicweather.messages.clear"));
        magicweather_message_thunder = color(config.getString("magicweather.messages.thunder"));
        magicweather_message_none = color(config.getString("magicweather.messages.none"));

        // Global initialization
        global_message_on = color(config.getString("global.messages.active"));
        global_message_off = color(config.getString("global.messages.deactivated"));
        global_message_reloaded = color(config.getString("global.messages.reloaded"));
        global_message_noperms = color(config.getString("global.messages.noperms"));
        global_message_notenoughmoney = color(config.getString("global.messages.notenoughmoney"));
        global_message_cooldown = color(config.getString("global.messages.cooldown"));
        global_message_playeronly = color(config.getString("global.messages.playeronly"));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
