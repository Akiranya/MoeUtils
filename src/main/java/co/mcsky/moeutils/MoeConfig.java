package co.mcsky.moeutils;

import co.mcsky.moeutils.config.YamlConfigFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static co.mcsky.moeutils.MoeUtils.plugin;

public final class MoeConfig {

    public static final String DEFAULT_LANG = "zh_cn";
    private static final String FILENAME = "config.yml";

    public boolean debug;

    public boolean found_ores_enabled;
    public int max_iterations;
    public int purge_interval;
    public List<Material> enabled_blocks;
    public List<String> enabled_worlds;

    public int magic_time_cooldown;
    public int magic_time_cost;
    public int magic_weather_cooldown;
    public int magic_weather_cost;

    public boolean better_bees_enabled;
    public boolean require_sneak;
    public boolean better_portals_enabled;
    public boolean death_logger_enabled;
    public int search_radius;
    public List<EntityType> logged_creatures;
    public boolean end_eye_changer_enabled;
    public List<Location> gate_locations;
    public boolean login_protection_enabled;
    public int login_protection_duration;
    public int login_protection_amplifier;

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public MoeConfig() {
        loader = YamlConfigFactory.loader(new File(plugin.getDataFolder(), FILENAME));
    }

    /**
     * Load config from file, assigning the config contents to root
     */
    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        /* initialize default config nodes */

        try {
            debug = root.node("debug").getBoolean(false);
            found_ores_enabled = root.node("found-ores", "enabled").getBoolean(false);
            max_iterations = root.node("found-ores", "max-iterations").getInt(32);
            purge_interval = root.node("found-ores", "purge-interval").getInt(1800);
            enabled_blocks = root.node("found-ores", "blocks").getList(Material.class, () -> List.of(Material.DIAMOND_ORE));
            enabled_worlds = root.node("found-ores", "worlds").getList(String.class, () -> List.of("world"));

            magic_time_cooldown = root.node("magic-time", "cooldown").getInt(600);
            magic_time_cost = root.node("magic-time", "cost").getInt(50);
            magic_weather_cooldown = root.node("magic-weather", "cooldown").getInt(600);
            magic_weather_cost = root.node("magic-weather", "cost").getInt(50);

            better_bees_enabled = root.node("better-bees", "enabled").getBoolean(false);
            require_sneak = root.node("better-bees", "require-sneak").getBoolean(false);

            better_portals_enabled = root.node("better-portals", "enabled").getBoolean(false);

            death_logger_enabled = root.node("death-logger", "enabled").getBoolean(false);
            search_radius = root.node("death-logger", "search-radius").getInt(32);
            logged_creatures = root.node("death-logger", "creatures").getList(EntityType.class, List.of(EntityType.VILLAGER));

            end_eye_changer_enabled = root.node("end-eye-changer", "enabled").getBoolean(false);
            gate_locations = root.node("end-eye-changer", "gate-locations").getList(Location.class, new ArrayList<>());

            login_protection_enabled = root.node("login-protection", "enabled").getBoolean(false);
            login_protection_duration = root.node("login-protection", "duration-in-sec").getInt(15);
            login_protection_amplifier = root.node("login-protection", "amplifier").getInt(4);

        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
        }

    }

    /**
     * Save root node to file
     */
    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

}
