package co.mcsky.moeutils.config;

import co.mcsky.moeutils.config.converter.EntityTypeConverter;
import co.mcsky.moeutils.config.converter.MaterialConverter;
import net.cubespace.Yamler.Config.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static co.mcsky.moeutils.MoeUtils.plugin;

@SuppressWarnings("CanBeFinal")
@SerializeOptions(configMode = ConfigMode.PATH_BY_UNDERSCORE)
public class Configuration extends YamlConfig {

    /*
     * MagicTime Config
     * */
    @Path("magictime.cost")
    public int magictime_cost = 50;
    @Path("magictime.cooldown")
    public int magictime_cooldown = 600;

    /*
     * MagicWeather Config
     * */
    @Path("magicweather.cost")
    public int magicweather_cost = 50;
    @Path("magicweather.cooldown")
    public int magicweather_cooldown = 600;

    /*
     * BetterBees Config
     * */
    @Path("betterbees.enable")
    public boolean betterbees_enable = true;
    @Path("betterbees.requireSneak")
    public boolean betterbees_requireSneak = false;

    /*
     * BetterPortals Config
     * */
    @Path("betterportals.enable")
    public boolean betterportals_enable = true;
    @Path("betterportals.debug")
    public boolean betterportals_debug = true;

    /*
     * DeathLogger Config
     * */
    @Path("deathlogger.enable")
    public boolean deathlogger_enable = true;
    @Path("deathlogger.searchRadius")
    @Comments({"The radius of searching for nearby players around the death entity.",
               "The search only happens if there is no direct killer to the death entity."})
    public int deathlogger_searchRadius = 16;
    @Path("deathlogger.creatures")
    @Comment("List of creatures that should be logged when died.")
    public java.util.Set<EntityType> deathlogger_creatures = new HashSet<>() {{
        add(EntityType.VILLAGER);
    }};

    /*
     * FoundOres Config
     * */
    @Path("foundores.enable")
    public boolean foundores_enable = true;
    @Path("foundores.maxIterations")
    @Comment("Max search attempts. Higher values may cause more lag.")
    public int foundores_maxIterations = 32;
    @Path("foundores.purgeInterval")
    @Comment("The interval (in second) between this plugin resets location history.")
    public int foundores_purgeInterval = 1800;
    @Path("foundores.worlds")
    @Comment("List of worlds where you want to enable announcement.")
    public java.util.Set<String> foundores_worlds = new HashSet<>() {{
        add("world");
        add("world_nether");
        add("world_the_end");
    }};
    @Path("foundores.blocks")
    @Comment("List of blocks which need to be announced.")
    public Set<Material> foundores_blocks = new HashSet<>() {{
        add(Material.GOLD_ORE);
        add(Material.IRON_ORE);
        add(Material.COAL_ORE);
        add(Material.LAPIS_ORE);
        add(Material.DIAMOND_ORE);
        add(Material.REDSTONE_ORE);
        add(Material.EMERALD_ORE);
        add(Material.NETHER_QUARTZ_ORE);
    }};

    /*
     * MobArena Addon Config
     * */
    @Path("mobarena.enable")
    public boolean mobarena_enable = true;
    @Path("mobarena.whitelist")
    @Comment("List of creatures that DO NOT collide with the arrows from players.")
    public java.util.Set<EntityType> mobarena_whitelist = new HashSet<>() {{
        add(EntityType.PLAYER);
        add(EntityType.WOLF);
        add(EntityType.OCELOT);
        add(EntityType.IRON_GOLEM);
        add(EntityType.HORSE);
    }};

    public Configuration() {
        try {
            CONFIG_HEADER = new String[]{"Configuration of MoeUtils"};
            CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
            addConverter(EntityTypeConverter.class);
            addConverter(MaterialConverter.class);
            init();
        } catch (InvalidConfigurationException | InvalidConverterException e) {
            e.printStackTrace();
        }
    }

    public void print() {
        final String bullet = " - ";
        plugin.getLogger().info(ChatColor.YELLOW + "FoundOres.blocks:");
        foundores_blocks.forEach(e -> plugin.getLogger().info(bullet + e.toString().toLowerCase()));
        plugin.getLogger().info(ChatColor.YELLOW + "FoundOres.worlds:");
        foundores_worlds.forEach(e -> plugin.getLogger().info(bullet + e));
        plugin.getLogger().info(ChatColor.YELLOW + "MobArena-Addon.whitelist:");
        mobarena_whitelist.forEach(e -> plugin.getLogger().info(bullet + e.toString().toLowerCase()));
    }

}
