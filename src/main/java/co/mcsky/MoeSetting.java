package co.mcsky;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import java.util.*;
import java.util.stream.Collectors;

public class MoeSetting {

    private static MoeSetting moeSetting = null;
    private final MoeUtils moe;

    public final Globe globe;
    public final MagicWeather magic_weather;
    public final MagicTime magic_time;
    public final Reminder reminder;
    public final SafePortal safe_portal;
    public final FoundDiamond found_diamond;
    public final MobArenaAddon mobarena;

    public static MoeSetting getInstance(MoeUtils moe) {
        if (moeSetting == null) return moeSetting = new MoeSetting(moe);
        return moeSetting;
    }

    private MoeSetting(MoeUtils moe) {
        this.moe = moe;

        globe = new Globe();
        magic_weather = new MagicWeather();
        magic_time = new MagicTime();
        reminder = new Reminder();
        safe_portal = new SafePortal();
        found_diamond = new FoundDiamond();
        mobarena = new MobArenaAddon();

        settingOutput(); // output 必须在最后执行，否则可能 NPE
    }

    public void reloadConfig() {
        // Reload from file
        moe.reloadConfig();

        // 这里新建一个对象就相当于重载配置
        moeSetting = new MoeSetting(moe);
    }

    public class Globe {
        public final String msg_none;
        public final String msg_reload;
        public final String msg_no_perm;
        public final String msg_not_enough_money;
        public final String msg_cooldown;
        public final String msg_on;
        public final String msg_off;
        public final String msg_player_only;
        public final String msg_reset;
        public final String msg_cost;

        private Globe() {
            msg_none = format("global.messages.none");
            msg_reload = format("global.messages.reloaded");
            msg_no_perm = format("global.messages.noperms");
            msg_not_enough_money = format("global.messages.notenoughmoney");
            msg_cooldown = format("global.messages.cooldown");
            msg_on = format("global.messages.active");
            msg_off = format("global.messages.deactivated");
            msg_player_only = format("global.messages.playeronly");
            msg_reset = format("global.messages.reset");
            msg_cost = format("global.messages.cost");
        }
    }

    public class MagicWeather {
        public final int cost;
        public final int cooldown;
        public final String msg_prefix;
        public final String msg_changed;
        public final String msg_finished;
        public final String msg_status;
        public final String msg_rain;
        public final String msg_clear;
        public final String msg_thunder;

        private MagicWeather() {
            cost = getInt("magicweather.cost");
            cooldown = getInt("magicweather.cooldown");

            msg_prefix = format("magicweather.prefix");
            msg_changed = format("magicweather.messages.changed");
            msg_finished = format("magicweather.messages.ended");
            msg_status = format("magicweather.messages.status");
            msg_rain = format("magicweather.messages.rain");
            msg_clear = format("magicweather.messages.clear");
            msg_thunder = format("magicweather.messages.thunder");
        }
    }

    public class MagicTime {
        public final int cost;
        public final int cooldown;

        public final String msg_day;
        public final String msg_night;
        public final String msg_prefix;
        public final String msg_status;
        public final String msg_ended;
        public final String msg_changed;

        private MagicTime() {
            cost = getInt("magictime.cost");
            cooldown = getInt("magictime.cooldown");

            msg_prefix = format("magictime.prefix");
            msg_ended = format("magictime.messages.ended");
            msg_changed = format("magictime.messages.changed");
            msg_status = format("magictime.messages.status");
            msg_day = format("magictime.messages.day");
            msg_night = format("magictime.messages.night");
        }
    }

    public class SafePortal {
        public final boolean enable;
        public final boolean debug;
        public final EventPriority priority;

        public final String msg_player;
        public final String msg_debug;

        private SafePortal() {
            enable = getBoolean("safe-portal.enable");
            debug = getBoolean("safe-portal.debug");
            priority = EventPriority.valueOf(getString("safe-portal.event-priority"));

            msg_player = format("safe-portal.messages.cancelled");
            msg_debug = format("safe-portal.messages.debug");
        }

    }

    public class FoundDiamond {
        public final boolean enable;
        public final Set<String> enabled_world;
        public final int max_iterations;
        public final int purge_interval;
        public final Map<Material, String> enabled_block_type;

        public final String msg_found;
        public final String msg_prefix;

        private FoundDiamond() {
            enable = getBoolean("foundores.enable");
            max_iterations = getInt("foundores.max_iterations");
            purge_interval = getInt("foundores.purge_interval");
            enabled_world = new HashSet<>(getStringList("foundores.worlds")); // use HashSet for constant searching time

            // 加载需要进行通报的方块类型
            enabled_block_type = new HashMap<>();
            Map<String, Object> rawMap = getConfigurationSection("foundores.block_types").getValues(false);
            rawMap.forEach((block, displayName) -> enabled_block_type.put(Material.matchMaterial(block), (String) displayName));

            msg_prefix = format("foundores.prefix");
            msg_found = format("foundores.messages.found");
        }
    }

    public class Reminder {
        public boolean enable;
        public Set<EntityType> whitelist;

        public String msg_death;

        private Reminder() {
            List<String> rawConfig = getStringList("reminder.animals");
            enable = getBoolean("reminder.enable");
            whitelist = rawConfig.stream()
                    .map(e -> EntityType.valueOf(e.toUpperCase()))
                    .collect(Collectors.toSet());

            msg_death = getString("reminder.messages.death");
        }
    }

    public class MobArenaAddon {
        public boolean enable;
        public Set<EntityType> whitelist;

        MobArenaAddon() {
            enable = getBoolean("mobarena-addon.enable");
            List<String> entityTypes = getStringList("mobarena-addon.whitelist");
            whitelist = entityTypes.stream()
                    .map(e -> EntityType.valueOf(e.toUpperCase()))
                    .collect(Collectors.toSet());
        }
    }

    private void settingOutput() {
        // FoundDiamond: output what types of block are to be announced
        moe.getLogger().info(ChatColor.YELLOW + "foundores.block_types:");
        found_diamond.enabled_block_type.forEach((k, v) -> moe.getLogger().info("- " + k.toString() + ": " + v));
        // FoundDiamond: output which worlds are enabled
        moe.getLogger().info(ChatColor.YELLOW + "foundores.worlds:");
        found_diamond.enabled_world.forEach(e -> moe.getLogger().info("- " + e));

        // MobArena: output which entities should bypass collision with players' arrows
        moe.getLogger().info(ChatColor.YELLOW + "mobarena.whitelist:");
        mobarena.whitelist.forEach(e -> moe.getLogger().info("- " + e.toString()));
    }

    private String format(String path) {
        Validate.notNull(moe.getConfig().getString(path), path + " does not exist.");
        return ChatColor.translateAlternateColorCodes('&', moe.getConfig().getString(path));
    }

    private String getString(String path) {
        return moe.getConfig().getString(path);
    }

    private List<String> getStringList(String path) {
        return moe.getConfig().getStringList(path);
    }

    private ConfigurationSection getConfigurationSection(String path) {
        return moe.getConfig().getConfigurationSection(path);
    }

    private int getInt(String path) {
        return moe.getConfig().getInt(path);
    }

    private boolean getBoolean(String path) {
        return moe.getConfig().getBoolean(path);
    }
}
