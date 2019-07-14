package co.mcsky;

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

    private MoeUtils plugin;
    private static Configuration configuration = null;

    // MobArena Addon
    public boolean MOBARENA_ON;
    public Set<EntityType> MOBARENA_WHITELIST;

    // Safe Portal
    public boolean SAFEPORTAL_ON;
    public boolean SAFEPORTAL_DEBUG_ON;
    public EventPriority SAFEPORTAL_PRIORITY;
    public String SAFEPORTAL_MESSAGE_PLAYER;
    public String SAFEPORTAL_MESSAGE_DEBUG;

    // MagicTime
    public int MAGICTIME_COST;
    public int MAGICTIME_COOLDOWN;
    public String MAGICTIME_MESSAGE_PREFIX;
    public String MAGICTIME_MESSAGE_TIMETODAY;
    public String MAGICTIME_MESSAGE_TIMETONIGHT;
    public String MAGICTIME_MESSAGE_DAYENDED;
    public String MAGICTIME_MESSAGE_NIGHTENDED;
    public String MAGICTIME_MESSAGE_TIMERRESET;
    public String MAGICTIME_MESSAGE_COST;
    public String MAGICTIME_MESSAGE_STATUS;

    // MagicWeather

    // Global
    public String GLOBAL_RELOADED;
    public String GLOBAL_MESSAGE_NOPERMS;
    public String GLOBAL_MESSAGE_NOTENOUGHMONEY;
    public String GLOBAL_MESSAGE_COOLDOWN;
    public String GLOBAL_MESSAGE_YES;
    public String GLOBAL_MESSAGE_NO;

    private Configuration(MoeUtils plugin) {
        this.plugin = plugin;
        loadFile();
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
        SAFEPORTAL_MESSAGE_PLAYER = plugin.getConfig().getString("safe-portal.messages.cancelled");
        SAFEPORTAL_MESSAGE_DEBUG = plugin.getConfig().getString("safe-portal.messages.debug");

        /* MagicTime */
        MAGICTIME_COST = plugin.getConfig().getInt("magictime.cost");
        MAGICTIME_COOLDOWN = plugin.getConfig().getInt("magictime.cooldown");
        MAGICTIME_MESSAGE_PREFIX = plugin.getConfig().getString("magictime.prefix");
        MAGICTIME_MESSAGE_COST = plugin.getConfig().getString("magictime.messages.cost");
        MAGICTIME_MESSAGE_DAYENDED = plugin.getConfig().getString("magictime.messages.dayended");
        MAGICTIME_MESSAGE_NIGHTENDED = plugin.getConfig().getString("magictime.messages.nightended");
        MAGICTIME_MESSAGE_TIMETODAY = plugin.getConfig().getString("magictime.messages.timetoday");
        MAGICTIME_MESSAGE_TIMETONIGHT = plugin.getConfig().getString("magictime.messages.timetonight");
        MAGICTIME_MESSAGE_STATUS = plugin.getConfig().getString("magictime.messages.status");
        MAGICTIME_MESSAGE_TIMERRESET = plugin.getConfig().getString("magictime.messages.reset");

        /* MagicWeather */

        // Global initialization
        GLOBAL_RELOADED = plugin.getConfig().getString("global.messages.reloaded");
        GLOBAL_MESSAGE_NOPERMS = plugin.getConfig().getString("global.messages.noperms");
        GLOBAL_MESSAGE_NOTENOUGHMONEY = plugin.getConfig().getString("global.messages.notenoughmoney");
        GLOBAL_MESSAGE_COOLDOWN = plugin.getConfig().getString("global.messages.cooldown");
        GLOBAL_MESSAGE_YES = plugin.getConfig().getString("global.messages.yes");
        GLOBAL_MESSAGE_NO = plugin.getConfig().getString("global.messages.no");
    }

    public static Configuration getInstance(MoeUtils plugin) {
        if (configuration == null) {
            configuration = new Configuration(plugin);
        }
        return configuration;
    }
}
