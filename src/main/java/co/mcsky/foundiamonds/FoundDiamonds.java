package co.mcsky.foundiamonds;

import co.mcsky.MoeUtils;
import co.mcsky.config.FoundDiamondsConfig;
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

public class FoundDiamonds implements Listener {

    final private MoeUtils moe;
    final private FoundDiamondsConfig cfg;

    final private Set<Material> enabledBlocks;
    final private Set<Location> locationHistory;
    final private BlockCounter blockCounter;

    public FoundDiamonds(MoeUtils moe) {
        this.moe = moe;
        this.cfg = moe.foundDiamondsConfig;
        enabledBlocks = cfg.blocks;
        locationHistory = new HashSet<>();
        blockCounter = new BlockCounter(moe);
        if (cfg.enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("FoundDiamonds is enabled");
        }
        // Auto clear map locationHistory at given interval.
        Bukkit.getScheduler().runTaskTimer(moe, locationHistory::clear, 0, TimeConverter.toTick(cfg.purgeInterval));
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!enabledBlocks.contains(block.getType())) return;
        if (!cfg.worlds.contains(block.getWorld().getName())) return;

        Location currentLocation = block.getLocation();
        Material currentBlock = block.getType();
        if (!locationHistory.contains(currentLocation)) {
            moe.getServer().broadcastMessage(cfg.msg_prefix + String.format(
                    cfg.msg_found,
                    event.getPlayer().getDisplayName(),
                    blockCounter.count(currentLocation, currentBlock, locationHistory),
                    I18nBlock.getBlockDisplayName(currentBlock, event.getPlayer())));
        }
    }

}
