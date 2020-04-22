package co.mcsky.foundores;

import co.mcsky.config.Configuration;
import co.mcsky.LanguageRepository;
import co.mcsky.MoeUtils;
import co.mcsky.i18n.I18nBlock;
import co.mcsky.utilities.TimeConverter;
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

    final private MoeUtils moe;
    final private LanguageRepository lang;
    final private Configuration config;

    private final Set<Material> enabledBlocks;
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    public FoundOres(MoeUtils moe) {
        this.moe = moe;
        this.lang = moe.lang;
        this.config = moe.config;
        this.enabledBlocks = config.foundores_blocks;
        this.locationHistory = new HashSet<>();
        this.blockCounter = new BlockCounter(config.foundores_maxIterations);
        if (config.foundores_enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("FoundDiamonds is enabled.");
            // Auto clear map locationHistory at given interval for performance reason.
            Bukkit.getScheduler().runTaskTimer(moe, locationHistory::clear, 0, TimeConverter.toTick(config.foundores_purgeInterval));
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
            moe.getServer().broadcastMessage(lang.foundores_prefix + String.format(
                    lang.foundores_found,
                    event.getPlayer().getDisplayName(),
                    blockCounter.count(currentLocation, currentBlock, locationHistory),
                    I18nBlock.getBlockDisplayName(currentBlock, lang)));
        }
    }

}
