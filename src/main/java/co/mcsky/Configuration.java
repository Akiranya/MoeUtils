package co.mcsky;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a singleton class, i.e. there is only one instance existing at the same time.<br>
 * All configurations should be listed here as members of this class.
 */
public class Configuration {

    private static Configuration configuration = null;
    /* MobArena Addon */
    public boolean MOBARENA_ON;
    public Set<EntityType> MOBARENA_WHITELIST;
    /* Safe Portal */
    public boolean SAFEPORTAL_ON;
    public boolean SAFEPORTAL_DEBUG_ON;
    public EventPriority SAFEPORTAL_PRIORITY;
    public String SAFEPORTAL_MESSAGE_PLAYER;
    public String SAFEPORTAL_MESSAGE_DEBUG;
    /* MagicTime */
    public int MAGICTIME_COST;
    public int MAGICTIME_COOLDOWN;
    public String MAGICTIME_MESSAGE_DAY;
    public String MAGICTIME_MESSAGE_NIGHT;
    public String MAGICTIME_MESSAGE_PREFIX;
    public String MAGICTIME_MESSAGE_RESET;
    public String MAGICTIME_MESSAGE_COST;
    public String MAGICTIME_MESSAGE_STATUS;
    public String MAGICTIME_MESSAGE_ENDED;
    public String MAGICTIME_MESSAGE_CHANGED;
    /* MagicWeather */
    public int MAGICWEATHER_COST;
    public int MAGICWEATHER_COOLDOWN;
    public String MAGICWEATHER_MESSAGE_PREFIX;
    public String MAGICWEATHER_MESSAGE_CHANGED;
    public String MAGICWEATHER_MESSAGE_ENDED;
    public String MAGICWEATHER_MESSAGE_RESET;
    public String MAGICWEATHER_MESSAGE_STATUS;
    public String MAGICWEATHER_MESSAGE_COST;
    public String MAGICWEATHER_MESSAGE_RAIN;
    public String MAGICWEATHER_MESSAGE_CLEAR;
    public String MAGICWEATHER_MESSAGE_THUNDER;
    public String MAGICWEATHER_MESSAGE_NONE;
    /* Global */
    public String GLOBAL_RELOADED;
    public String GLOBAL_MESSAGE_NOPERMS;
    public String GLOBAL_MESSAGE_NOTENOUGHMONEY;
    public String GLOBAL_MESSAGE_COOLDOWN;
    public String GLOBAL_MESSAGE_ON;
    public String GLOBAL_MESSAGE_OFF;
    public String GLOBAL_MESSAGE_PLAYERONLY;
    private MoeUtils plugin;

    private Configuration(MoeUtils plugin) {
        this.plugin = plugin;
        loadFile();
    }

    public static Configuration getInstance(MoeUtils plugin) {
        if (configuration == null) {
            configuration = new Configuration(plugin);
        }
        return configuration;
    }

    public void loadFile() {
        plugin.reloadConfig();

        // MobArena Addon initialization
        MOBARENA_ON = plugin.getConfig().getBoolean("mobarena-addon.enable");
        List<String> entityTypes = plugin.getConfig().getStringList("mobarena-addon.whitelist");
        MOBARENA_WHITELIST = entityTypes.stream()
                .map(e -> EntityType.valueOf(e.toUpperCase()))
                .collect(Collectors.toSet());
        // Print out MOBARENA_WHITELIST for double-checking
        MOBARENA_WHITELIST.forEach(e -> plugin.getLogger().info(e.name()));

        // Safe Portal initialization
        SAFEPORTAL_ON = plugin.getConfig().getBoolean("safe-portal.enable");
        SAFEPORTAL_DEBUG_ON = plugin.getConfig().getBoolean("safe-portal.debug");
        SAFEPORTAL_PRIORITY = EventPriority.valueOf(plugin.getConfig().getString("safe-portal.event-priority"));
        SAFEPORTAL_MESSAGE_PLAYER = color(plugin.getConfig().getString("safe-portal.messages.cancelled"));
        SAFEPORTAL_MESSAGE_DEBUG = color(plugin.getConfig().getString("safe-portal.messages.debug"));

        /* MagicTime */
        MAGICTIME_COST = plugin.getConfig().getInt("magictime.cost");
        MAGICTIME_COOLDOWN = plugin.getConfig().getInt("magictime.cooldown");
        MAGICTIME_MESSAGE_PREFIX = color(plugin.getConfig().getString("magictime.prefix"));
        MAGICTIME_MESSAGE_COST = color(plugin.getConfig().getString("magictime.messages.cost"));
        MAGICTIME_MESSAGE_ENDED = color(plugin.getConfig().getString("magictime.messages.ended"));
        MAGICTIME_MESSAGE_CHANGED = color(plugin.getConfig().getString("magictime.messages.changed"));
        MAGICTIME_MESSAGE_STATUS = color(plugin.getConfig().getString("magictime.messages.status"));
        MAGICTIME_MESSAGE_RESET = color(plugin.getConfig().getString("magictime.messages.reset"));
        MAGICTIME_MESSAGE_DAY = color(plugin.getConfig().getString("magictime.messages.day"));
        MAGICTIME_MESSAGE_NIGHT = color(plugin.getConfig().getString("magictime.messages.night"));

        /* MagicWeather */
        MAGICWEATHER_COST = plugin.getConfig().getInt("magicweather.cost");
        MAGICWEATHER_COOLDOWN = plugin.getConfig().getInt("magicweather.cooldown");
        MAGICWEATHER_MESSAGE_PREFIX = color(plugin.getConfig().getString("magicweather.prefix"));
        MAGICWEATHER_MESSAGE_CHANGED = color(plugin.getConfig().getString("magicweather.messages.changed"));
        MAGICWEATHER_MESSAGE_ENDED = color(plugin.getConfig().getString("magicweather.messages.ended"));
        MAGICWEATHER_MESSAGE_STATUS = color(plugin.getConfig().getString("magicweather.messages.status"));
        MAGICWEATHER_MESSAGE_COST = color(plugin.getConfig().getString("magicweather.messages.cost"));
        MAGICWEATHER_MESSAGE_RESET = color(plugin.getConfig().getString("magicweather.messages.reset"));
        MAGICWEATHER_MESSAGE_RAIN = color(plugin.getConfig().getString("magicweather.messages.rain"));
        MAGICWEATHER_MESSAGE_CLEAR = color(plugin.getConfig().getString("magicweather.messages.clear"));
        MAGICWEATHER_MESSAGE_THUNDER = color(plugin.getConfig().getString("magicweather.messages.thunder"));
        MAGICWEATHER_MESSAGE_NONE = color(plugin.getConfig().getString("magicweather.messages.none"));

        // Global initialization
        GLOBAL_MESSAGE_ON = color(plugin.getConfig().getString("global.messages.active"));
        GLOBAL_MESSAGE_OFF = color(plugin.getConfig().getString("global.messages.deactivated"));
        GLOBAL_RELOADED = color(plugin.getConfig().getString("global.messages.reloaded"));
        GLOBAL_MESSAGE_NOPERMS = color(plugin.getConfig().getString("global.messages.noperms"));
        GLOBAL_MESSAGE_NOTENOUGHMONEY = color(plugin.getConfig().getString("global.messages.notenoughmoney"));
        GLOBAL_MESSAGE_COOLDOWN = color(plugin.getConfig().getString("global.messages.cooldown"));
        GLOBAL_MESSAGE_PLAYERONLY = color(plugin.getConfig().getString("global.messages.playeronly"));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
