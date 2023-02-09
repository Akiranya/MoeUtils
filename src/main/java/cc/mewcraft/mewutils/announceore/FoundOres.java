package cc.mewcraft.mewutils.announceore;

import cc.mewcraft.mewutils.MewUtils;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
    public void toggleBroadcast(UUID player) {
        final MetadataMap map = Metadata.provideForPlayer(player);
        map.put(BROADCAST_TOGGLE_KEY, !isListener(player));
    }

    public boolean isListener(UUID player) {
        final MetadataMap map = Metadata.provideForPlayer(player);
        return map.getOrPut(BROADCAST_TOGGLE_KEY, () -> true);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MewUtils.logModule("FoundOres", MewUtils.config().found_ores_enabled))
            return;

        // clear history locations at interval
        Schedulers.sync().runRepeating(locationHistory::clear, 5, Ticks.from(MewUtils.config().purge_interval, TimeUnit.SECONDS));

        Events.subscribe(BlockBreakEvent.class)
            .filter(e -> hashEnabledBlocks.contains(e.getBlock().getType()))
            .filter(e -> hashEnabledWorld.contains(e.getBlock().getWorld().getName()))
            .filter(e -> !locationHistory.contains(e.getBlock().getLocation()))
            .handler(e -> {
                Component prefix = MewUtils.translations().of("found_ores.prefix").component();
                Component message = MewUtils.translations().of("found_ores.found")
                    .resolver(Placeholder.component("player", e.getPlayer().displayName()))
                    .resolver(Placeholder.component("ore", Component.text(e.getBlock().getType().translationKey())))
                    .replace("count", blockCounter.count(e.getBlock().getLocation(), e.getBlock().getType(), locationHistory))
                    .component();
                Bukkit.getServer()
                    .filterAudience(audience -> isListener(e.getPlayer().getUniqueId()))
                    .sendMessage(prefix.append(message));
            }).bindWith(consumer);
    }
}
