package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.i18n.I18nBlock;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class FoundOres implements TerminableModule {

    // necessary for this function to work
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    // wrap arraylist with hashset for faster lookup
    private final Set<Material> hashEnabledBlocks;
    private final Set<String> hashEnabledWorld;

    public FoundOres() {
        locationHistory = new HashSet<>();
        blockCounter = new BlockCounter(plugin.config.max_iterations);

        hashEnabledBlocks = new HashSet<>();
        hashEnabledWorld = new HashSet<>();
        hashEnabledBlocks.addAll(plugin.config.enabled_blocks);
        hashEnabledWorld.addAll(plugin.config.enabled_worlds);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!MoeUtils.logActiveStatus("FoundOres", plugin.config.found_ores_enabled)) return;

        // clear history locations at interval
        Schedulers.sync().runRepeating(locationHistory::clear, 0, Ticks.from(plugin.config.purge_interval, TimeUnit.SECONDS));

        Events.subscribe(BlockBreakEvent.class)
                .filter(e -> hashEnabledBlocks.contains(e.getBlock().getType()))
                .filter(e -> hashEnabledWorld.contains(e.getBlock().getWorld().getName()))
                .filter(e -> !locationHistory.contains(e.getBlock().getLocation()))
                .handler(e -> {
                    final Block currentBlock = e.getBlock();
                    String prefix = plugin.getMessage(e.getPlayer(), "found-ores.prefix");
                    String message = plugin.getMessage(e.getPlayer(), "found-ores.found",
                            "player", e.getPlayer().getDisplayName(),
                            "count", blockCounter.count(currentBlock.getLocation(), currentBlock.getType(), locationHistory),
                            "ore", I18nBlock.getBlockDisplayName(currentBlock.getType()));
                    plugin.getServer().broadcastMessage(prefix + message);
                }).bindWith(consumer);
    }
}
