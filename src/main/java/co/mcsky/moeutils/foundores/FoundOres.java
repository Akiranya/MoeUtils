package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.config.Config;
import co.mcsky.moeutils.i18n.I18nBlock;
import co.mcsky.moeutils.utilities.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashSet;
import java.util.Set;

public class FoundOres implements Listener {

    public final MoeUtils plugin;
    public final Config config;

    private final Set<Material> enabledBlocks;
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    public FoundOres(MoeUtils plugin) {
        this.plugin = plugin;
        this.config = plugin.config;
        this.enabledBlocks = config.FOUNDORES_BLOCKS;
        this.locationHistory = new HashSet<>();
        this.blockCounter = new BlockCounter(config.FOUNDORES_MAX_ITERATIONS);
        if (config.FOUNDORES_ENABLE) {
            this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
            this.plugin.getLogger().info("FoundOres is enabled.");
            // Auto clear map locationHistory at given interval for performance reason.
            Bukkit.getScheduler().runTaskTimer(this.plugin, locationHistory::clear, 0, TimeConverter.toTick(config.FOUNDORES_PURGE_INTERVAL));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!enabledBlocks.contains(block.getType()))
            return;
        if (!config.FOUNDORES_WORLDS.contains(block.getWorld().getName()))
            return;

        Location currentLocation = block.getLocation();
        Material currentBlock = block.getType();
        if (!locationHistory.contains(currentLocation)) {
            plugin.getServer().broadcastMessage(plugin.getMessage(plugin.getServer().getConsoleSender(), "foundores.prefix") + String.format(
                    plugin.getMessage(plugin.getServer().getConsoleSender(), "foundores.found"),
                    event.getPlayer().getDisplayName(),
                    blockCounter.count(currentLocation, currentBlock, locationHistory),
                    I18nBlock.getBlockDisplayName(currentBlock)));
        }
    }

}
