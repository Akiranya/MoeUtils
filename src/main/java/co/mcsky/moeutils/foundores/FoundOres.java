package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.LanguageRepository;
import co.mcsky.moeutils.config.Configuration;
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

import static co.mcsky.moeutils.MoeUtils.*;

public class FoundOres implements Listener {

    final private LanguageRepository lang;
    final private Configuration config;

    private final Set<Material> enabledBlocks;
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    public FoundOres() {
        this.lang = plugin.lang;
        this.config = plugin.config;
        this.enabledBlocks = config.foundores_blocks;
        this.locationHistory = new HashSet<>();
        this.blockCounter = new BlockCounter(config.foundores_maxIterations);
        if (config.foundores_enable) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("FoundOres is enabled.");
            // Auto clear map locationHistory at given interval for performance reason.
            Bukkit.getScheduler().runTaskTimer(plugin, locationHistory::clear, 0, TimeConverter.toTick(config.foundores_purgeInterval));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!enabledBlocks.contains(block.getType())) return;
        if (!config.foundores_worlds.contains(block.getWorld().getName())) return;

        Location currentLocation = block.getLocation();
        Material currentBlock = block.getType();
        if (!locationHistory.contains(currentLocation)) {
            plugin.getServer().broadcastMessage(lang.foundores_prefix + String.format(
                    lang.foundores_found,
                    event.getPlayer().getDisplayName(),
                    blockCounter.count(currentLocation, currentBlock, locationHistory),
                    I18nBlock.getBlockDisplayName(currentBlock)));
        }
    }

}
