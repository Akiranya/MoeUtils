package co.mcsky.moeutils.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static co.mcsky.moeutils.MoeUtils.plugin;

public final class Config {

    private final YamlConfigurationLoader loader;

    public int MAGICTIME_COST;
    public int MAGICTIME_COOLDOWN;
    public int MAGICWEATHER_COST;
    public int MAGICWEATHER_COOLDOWN;
    public boolean BETTERBEES_ENABLE;
    public boolean BETTERBEES_REQUIRE_SNEAK;
    public boolean BETTERPORTALS_ENABLE;
    public boolean BETTERPORTALS_DEBUG;
    public boolean DEATHLOGGER_ENABLE;
    public int DEATHLOGGER_SEARCH_RADIUS;
    public Set<EntityType> DEATHLOGGER_CREATURES;
    public boolean FOUNDORES_ENABLE;
    public int FOUNDORES_MAX_ITERATIONS;
    public int FOUNDORES_PURGE_INTERVAL;
    public Set<String> FOUNDORES_WORLDS;
    public Set<Material> FOUNDORES_BLOCKS;
    public boolean MOBARENA_ENABLE;
    public Set<EntityType> MOBARENA_WHITELIST;

    private CommentedConfigurationNode root;

    public Config() {
        // Initialize the loader with specified builder for config.yml
        loader = YamlConfigurationLoader.builder()
                                        .indent(4)
                                        .path(new File(plugin.getDataFolder(), "config.yml").toPath())
                                        .nodeStyle(NodeStyle.BLOCK)
                                        .build();

        // Load config from file, assigning the config tree to root
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Failed to load config.yml: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        // Native values
        MAGICWEATHER_COST = node("magictime", "cost").getInt(50);
        MAGICTIME_COOLDOWN = node("magictime", "cooldown").getInt(600);
        MAGICWEATHER_COST = node("magicweather", "cost").getInt(50);
        MAGICWEATHER_COOLDOWN = node("magicweather", "cooldown").getInt(600);
        BETTERBEES_ENABLE = node("betterbees", "enable").getBoolean();
        BETTERBEES_REQUIRE_SNEAK = node("betterbees", "requireSneak").getBoolean();
        BETTERPORTALS_ENABLE = node("betterportals", "enable").getBoolean();
        BETTERPORTALS_DEBUG = node("betterportals", "debug").getBoolean();
        DEATHLOGGER_ENABLE = node("deathlogger", "enable").getBoolean();
        DEATHLOGGER_SEARCH_RADIUS = node("deathlogger", "searchRadius").getInt(32);
        FOUNDORES_ENABLE = node("foundores", "enable").getBoolean();
        FOUNDORES_MAX_ITERATIONS = node("foundores", "maxIterations").getInt(32);
        FOUNDORES_PURGE_INTERVAL = node("foundores", "purgeInterval").getInt(1800);
        MOBARENA_ENABLE = node("mobarena", "enable").getBoolean();

        // Compound values
        try {
            FOUNDORES_WORLDS = new HashSet<>(node("foundores", "worlds").getList(String.class, () ->
                    List.of("world",
                            "world_nether",
                            "world_the_end")));
            FOUNDORES_BLOCKS = new HashSet<>(node("foundores", "blocks").getList(Material.class, () ->
                    List.of(Material.GOLD_ORE,
                            Material.IRON_ORE,
                            Material.COAL_ORE,
                            Material.LAPIS_ORE,
                            Material.DIAMOND_ORE,
                            Material.REDSTONE_ORE,
                            Material.EMERALD_ORE,
                            Material.NETHER_QUARTZ_ORE)));
            MOBARENA_WHITELIST = new HashSet<>(node("mobarena", "whitelist").getList(EntityType.class, () ->
                    List.of(EntityType.PLAYER,
                            EntityType.WOLF,
                            EntityType.OCELOT,
                            EntityType.IRON_GOLEM,
                            EntityType.HORSE)));
            DEATHLOGGER_CREATURES = new HashSet<>(node("deathlogger", "creatures").getList(EntityType.class, () ->
                    List.of(EntityType.VILLAGER)));
        } catch (SerializationException e) {
            plugin.getLogger().severe("A error occurred during serialization: " + e.getMessage());
            // 从文件载入多少 compound values，就算多少。这里仅仅 return
            return;
        }

        // Read and log important configs for double-check
        plugin.getLogger().info(ChatColor.YELLOW + "FoundOres.blocks:");
        FOUNDORES_BLOCKS.forEach(e -> plugin.getLogger().info(e.toString()));
        plugin.getLogger().info(ChatColor.YELLOW + "FoundOres.worlds:");
        FOUNDORES_WORLDS.forEach(e -> plugin.getLogger().info(e));
        plugin.getLogger().info(ChatColor.YELLOW + "MobArena-Addon.whitelist:");
        MOBARENA_WHITELIST.forEach(e -> plugin.getLogger().info(e.toString()));
        plugin.getLogger().info(ChatColor.YELLOW + "DeathLogger.creatures:");
        DEATHLOGGER_CREATURES.forEach(e -> plugin.getLogger().info(e.toString()));

        // Save config file
        save();
    }

    public CommentedConfigurationNode node(Object... path) {
        if (root == null) {
            plugin.getLogger().severe("Config is not loaded yet!");
            throw new IllegalStateException();
        }
        return root.node(path);
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Unable to save config.yml: " + e.getMessage());
        }
    }

}
