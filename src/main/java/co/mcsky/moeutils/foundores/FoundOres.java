package co.mcsky.moeutils.foundores;

import co.mcsky.moecore.text.Text;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
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
import org.bukkit.inventory.ItemStack;
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
        blockCounter = new BlockCounter(MoeUtils.config().max_iterations);

        hashEnabledBlocks = new HashSet<>();
        hashEnabledWorld = new HashSet<>();
        hashEnabledBlocks.addAll(MoeUtils.config().enabled_blocks);
        hashEnabledWorld.addAll(MoeUtils.config().enabled_worlds);

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
                    .expiry(MoeUtils.config().non_listener_expiry_hours, TimeUnit.HOURS)
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
        if (MoeUtils.logActiveStatus("FoundOres", MoeUtils.config().found_ores_enabled))
            return;

        // clear history locations at interval
        Schedulers.sync().runRepeating(locationHistory::clear, 5, Ticks.from(MoeUtils.config().purge_interval, TimeUnit.SECONDS));

        Events.subscribe(BlockBreakEvent.class)
                .filter(e -> hashEnabledBlocks.contains(e.getBlock().getType()))
                .filter(e -> hashEnabledWorld.contains(e.getBlock().getWorld().getName()))
                .filter(e -> !locationHistory.contains(e.getBlock().getLocation()))
                .handler(e -> {
                    final Text prefix = MoeUtils.text3("found-ores.prefix");
                    final Text message = prefix.append(MoeUtils.text3("found-ores.found")
                            .replace("player", e.getPlayer())
                            .replace("count", blockCounter.count(e.getBlock().getLocation(), e.getBlock().getType(), locationHistory))
                            .replace("ore", new ItemStack(e.getBlock().getType())));
                    prefix.append(message).broadcast(Text.MessageType.CHAT, this::isListener);
                }).bindWith(consumer);
    }
}
