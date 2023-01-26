package cc.mewcraft.mewutils.announceore;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewcore.text.Text;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FoundOres implements TerminableModule {

    public static final MetadataKey<Boolean> BROADCAST_TOGGLE_KEY = MetadataKey.createBooleanKey("subscribe.announcement.ore");

    // necessary for this function to work
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    // wrap arraylist with hashset for faster lookup
    private final Set<Material> hashEnabledBlocks;
    private final Set<String> hashEnabledWorld;

    public FoundOres() {
        locationHistory = new HashSet<>();
        blockCounter = new BlockCounter(MewUtils.config().max_iterations);

        hashEnabledBlocks = new HashSet<>();
        hashEnabledWorld = new HashSet<>();
        hashEnabledBlocks.addAll(MewUtils.config().enabled_blocks);
        hashEnabledWorld.addAll(MewUtils.config().enabled_worlds);
    }

    /**
     * Toggles the player whether to receive mining broadcast.
     */
    public void toggleBroadcast(Player player) {
        final MetadataMap map = Metadata.provideForPlayer(player);
        map.put(BROADCAST_TOGGLE_KEY, !isListener(player));
    }

    public boolean isListener(Player player) {
        final MetadataMap map = Metadata.provideForPlayer(player);
        return map.getOrPut(BROADCAST_TOGGLE_KEY, () -> true);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MewUtils.logStatus("FoundOres", MewUtils.config().found_ores_enabled))
            return;

        // clear history locations at interval
        Schedulers.sync().runRepeating(locationHistory::clear, 5, Ticks.from(MewUtils.config().purge_interval, TimeUnit.SECONDS));

        Events.subscribe(BlockBreakEvent.class)
                .filter(e -> hashEnabledBlocks.contains(e.getBlock().getType()))
                .filter(e -> hashEnabledWorld.contains(e.getBlock().getWorld().getName()))
                .filter(e -> !locationHistory.contains(e.getBlock().getLocation()))
                .handler(e -> {
                    final Text prefix = MewUtils.text3("found-ores.prefix");
                    final Text message = MewUtils.text3("found-ores.found")
                            .replace("player", e.getPlayer())
                            .replace("count", blockCounter.count(e.getBlock().getLocation(), e.getBlock().getType(), locationHistory))
                            .replace("ore", new ItemStack(e.getBlock().getType()));
                    prefix.append(message).broadcast(Text.MessageType.CHAT, this::isListener);
                }).bindWith(consumer);
    }
}
