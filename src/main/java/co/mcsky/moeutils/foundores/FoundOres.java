package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.util.I18nBlock;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.Players;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FoundOres implements TerminableModule {

    public static final String BROADCAST_TOGGLE_KEY = "moe:mining-listener";

    // necessary for this function to work
    private final Set<Location> locationHistory;
    private final BlockCounter blockCounter;

    // wrap arraylist with hashset for faster lookup
    private final Set<Material> hashEnabledBlocks;
    private final Set<String> hashEnabledWorld;

    // use metadata to store broadcast toggle
    private final LuckPerms luckPerms;

    public FoundOres() {
        locationHistory = new HashSet<>();
        blockCounter = new BlockCounter(MoeUtils.plugin.config.max_iterations);

        hashEnabledBlocks = new HashSet<>();
        hashEnabledWorld = new HashSet<>();
        hashEnabledBlocks.addAll(MoeUtils.plugin.config.enabled_blocks);
        hashEnabledWorld.addAll(MoeUtils.plugin.config.enabled_worlds);

        luckPerms = LuckPermsProvider.get();
    }

    /**
     * Toggles the player whether to receive mining broadcast.
     *
     * @param player the player
     */
    public void toggleBroadcast(Player player) {
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
            final Optional<Boolean> metaValue = user.getCachedData().getMetaData().getMetaValue(BROADCAST_TOGGLE_KEY, Boolean::parseBoolean);
            final MetaNode metaNode = MetaNode.builder()
                    .key(BROADCAST_TOGGLE_KEY)
                    .value(String.valueOf(!metaValue.orElse(true)))
                    .context(ImmutableContextSet.empty())
                    .expiry(MoeUtils.plugin.config.non_listener_expiry_hours, TimeUnit.HOURS)
                    .build();
            user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(BROADCAST_TOGGLE_KEY)));
            user.data().add(metaNode);
        });
    }

    /**
     * Checks whether the player is listening the mining broadcast.
     *
     * @param player the player
     * @return true if the player is listening to the broadcast, otherwise false
     */
    public boolean isListener(Player player) {
        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
        return metaData.getMetaValue(BROADCAST_TOGGLE_KEY, Boolean::parseBoolean).orElse(true);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("FoundOres", MoeUtils.plugin.config.found_ores_enabled))
            return;

        // clear history locations at interval
        Schedulers.sync().runRepeating(locationHistory::clear, 5, Ticks.from(MoeUtils.plugin.config.purge_interval, TimeUnit.SECONDS));

        Events.subscribe(BlockBreakEvent.class)
                .filter(e -> hashEnabledBlocks.contains(e.getBlock().getType()))
                .filter(e -> hashEnabledWorld.contains(e.getBlock().getWorld().getName()))
                .filter(e -> !locationHistory.contains(e.getBlock().getLocation()))
                .handler(e -> {
                    Component prefix = Component.text(MoeUtils.plugin.message(e.getPlayer(), "found-ores.prefix"));
                    Component message = Component.text(MoeUtils.plugin.message(e.getPlayer(), "found-ores.found",
                            "player", e.getPlayer().getName(),
                            "count", blockCounter.count(e.getBlock().getLocation(), e.getBlock().getType(), locationHistory),
                            "ore", I18nBlock.localizedName(e.getBlock().getType())));

                    // broadcast to each online player
                    Players.forEach(p -> {
                        if (isListener(p)) {
                            p.sendMessage(prefix.append(message));
                        }
                    });
                }).bindWith(consumer);
    }
}
