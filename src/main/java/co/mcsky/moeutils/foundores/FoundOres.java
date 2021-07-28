package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.i18n.I18nBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class FoundOres implements Listener {

    public static final String header = "foundores";
    public static boolean enable;
    public static int maxIteration;
    public static int purgeInterval;
    public static Set<Material> enabledBlocks;
    public static Set<String> enabledWorlds;

    public final MoeConfig config;

    private Set<Location> locationHistory;
    private BlockCounter blockCounter;

    public FoundOres() {
        config = plugin.config;

        // Configuration values
        enable = config.node(header, "enable").getBoolean();
        maxIteration = config.node(header, "maxIterations").getInt(32);
        purgeInterval = config.node(header, "purgeInterval").getInt(1800);

        try {
            enabledBlocks = new HashSet<>(config.node(header, "blocks").getList(Material.class, () ->
                    List.of(Material.GOLD_ORE,
                            Material.IRON_ORE,
                            Material.COAL_ORE,
                            Material.LAPIS_ORE,
                            Material.DIAMOND_ORE,
                            Material.REDSTONE_ORE,
                            Material.EMERALD_ORE,
                            Material.NETHER_QUARTZ_ORE)));
            enabledWorlds = new HashSet<>(config.node(header, "worlds").getList(String.class, () ->
                    List.of("world",
                            "world_nether",
                            "world_the_end")));
        } catch (final SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
            return;
        }

        // Internal variables
        locationHistory = new HashSet<>();
        blockCounter = new BlockCounter(maxIteration);

        if (enable) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("FoundOres is enabled.");
            // Auto clear map locationHistory at given interval for performance reason.
            Bukkit.getScheduler().runTaskTimer(plugin, locationHistory::clear, 0, purgeInterval * 20L);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!enabledBlocks.contains(block.getType()))
            return;
        if (!enabledWorlds.contains(block.getWorld().getName()))
            return;

        Location currentLocation = block.getLocation();
        Material currentBlock = block.getType();
        if (!locationHistory.contains(currentLocation)) {
            String prefix = plugin.getMessage(null, "foundores.prefix");
            String message = plugin.getMessage(null, "foundores.found",
                    "player", event.getPlayer().displayName(),
                    "count", blockCounter.count(currentLocation, currentBlock, locationHistory),
                    "ore", I18nBlock.getBlockDisplayName(currentBlock));
            plugin.getServer().broadcastMessage(prefix + message);
        }
    }

}
