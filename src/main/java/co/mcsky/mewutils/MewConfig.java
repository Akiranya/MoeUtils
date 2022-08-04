package co.mcsky.mewutils;

import co.mcsky.mewcore.config.YamlConfigFactory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.List;

public final class MewConfig {

    public static final String DEFAULT_LANG = "zh_cn";
    public static final String CONFIG_FILENAME = "config.yml";

    private final YamlConfigurationLoader loader;

    public boolean debug;

    // ore announcer
    public boolean found_ores_enabled;
    public int max_iterations;
    public int purge_interval;
    public List<Material> enabled_blocks;
    public List<String> enabled_worlds;
    public int non_listener_expiry_hours;

    // magic time and weather
    public int magic_time_cooldown;
    public int magic_time_cost;
    public int magic_weather_cooldown;
    public int magic_weather_cost;

    // better bees
    public boolean better_bees_enabled;
    public boolean require_sneak;

    // better portals
    public boolean better_portals_enabled;

    // death logger
    public boolean death_logger_enabled;
    public int search_radius;
    public List<EntityType> logged_creatures;

    // custom prefix
    public List<String> prefix_disabled_formatting_codes;
    public List<String> prefix_blocked_words;
    public int prefix_priority;
    public int prefix_max_length;
    public int prefix_exp_cost;
    public double prefix_money_cost;

    // custom suffix
    public List<String> suffix_disabled_formatting_codes;
    public List<String> suffix_blocked_words;
    public int suffix_priority;
    public int suffix_max_length;
    public int suffix_exp_cost;
    public double suffix_money_cost;

    // slow elytra
    public boolean slow_elytra_enabled;
    public List<String> slow_elytra_worlds;
    public double slow_elytra_tps_threshold;
    public double slow_elytra_velocity_multiply;
    public int slow_elytra_cooldown;

    private CommentedConfigurationNode root;

    public MewConfig() {
        loader = YamlConfigFactory.loader(new File(MewUtils.p.getDataFolder(), CONFIG_FILENAME));
    }

    /**
     * Load config from file, assigning the config contents to root
     */
    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            MewUtils.logger().severe(e.getMessage());
            MewUtils.p.getServer().getPluginManager().disablePlugin(MewUtils.p);
            return;
        }

        /* initialize default config nodes */

        try {
            debug = root.node("debug").getBoolean(false);

            final CommentedConfigurationNode foundOresNode = root.node("found-ores");
            found_ores_enabled = foundOresNode.node("enabled").getBoolean(false);
            max_iterations = foundOresNode.node("max-iterations").getInt(32);
            purge_interval = foundOresNode.node("purge-interval").getInt(1800);
            enabled_blocks = foundOresNode.node("blocks").getList(Material.class, () -> List.of(Material.DIAMOND_ORE));
            enabled_worlds = foundOresNode.node("worlds").getList(String.class, () -> List.of("world"));

            final CommentedConfigurationNode magicTimeNode = root.node("magic-time");
            magic_time_cooldown = magicTimeNode.node("cooldown").getInt(600);
            magic_time_cost = magicTimeNode.node("cost").getInt(50);

            final CommentedConfigurationNode magicWeatherNode = root.node("magic-weather");
            magic_weather_cooldown = magicWeatherNode.node("cooldown").getInt(600);
            magic_weather_cost = magicWeatherNode.node("cost").getInt(50);

            final CommentedConfigurationNode betterBeesNode = root.node("better-bees");
            better_bees_enabled = betterBeesNode.node("enabled").getBoolean(false);
            require_sneak = betterBeesNode.node("require-sneak").getBoolean(false);

            final CommentedConfigurationNode betterPortalsNode = root.node("better-portals");
            better_portals_enabled = betterPortalsNode.node("enabled").getBoolean(false);

            final CommentedConfigurationNode deathLoggerNode = root.node("death-logger");
            death_logger_enabled = deathLoggerNode.node("enabled").getBoolean(false);
            search_radius = deathLoggerNode.node("search-radius").getInt(32);
            logged_creatures = deathLoggerNode.node("creatures").getList(EntityType.class, List.of(EntityType.VILLAGER));

            final CommentedConfigurationNode prefixNode = root.node("prefix");
            prefix_disabled_formatting_codes = prefixNode.node("disabled-formatting-codes").getList(String.class);
            prefix_blocked_words = prefixNode.node("blocked-words").getList(String.class);
            prefix_priority = prefixNode.node("priority").getInt(10);
            prefix_max_length = prefixNode.node("max-length").getInt(10);
            prefix_exp_cost = prefixNode.node("exp-cost").getInt(100);
            prefix_money_cost = prefixNode.node("money-cost").getDouble(10);

            final CommentedConfigurationNode suffixNode = root.node("suffix");
            suffix_disabled_formatting_codes = suffixNode.node("disabled-formatting-codes").getList(String.class);
            suffix_blocked_words = suffixNode.node("blocked-words").getList(String.class);
            suffix_priority = suffixNode.node("priority").getInt(10);
            suffix_max_length = suffixNode.node("max-length").getInt(10);
            suffix_exp_cost = suffixNode.node("exp-cost").getInt(100);
            suffix_money_cost = suffixNode.node("money-cost").getDouble(10);

            final CommentedConfigurationNode slowElytraNode = root.node("slow-elytra");
            slow_elytra_enabled = slowElytraNode.node("enabled").getBoolean(true);
            slow_elytra_worlds = slowElytraNode.node("limited-worlds").getList(String.class, List.of("ex", "ex_nether", "ex_the_end"));
            slow_elytra_cooldown = slowElytraNode.node("cooldown").getInt(10000);
            slow_elytra_tps_threshold = slowElytraNode.node("tps-threshold").getDouble(19D);
            slow_elytra_velocity_multiply = slowElytraNode.node("velocity-multiply").getDouble(0.2);

        } catch (SerializationException e) {
            MewUtils.logger().severe(e.getMessage());
        }
    }

    /**
     * Save root node to file
     */
    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            MewUtils.logger().severe(e.getMessage());
        }
    }

}
