package cc.mewcraft.mewutils.module.oreannouncer;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OreAnnouncerModule extends ModuleBase implements AutoCloseableListener {

    public static final MetadataKey<Boolean> BROADCAST_TOGGLE_KEY = MetadataKey.createBooleanKey("subscribe.announcement.ore");

    private final BlockCounter blockCounter;
    private final Set<Material> enabledMaterials;
    private final Set<String> enabledWorlds;

    @Inject
    public OreAnnouncerModule(MewPlugin plugin) {
        super(plugin);

        this.blockCounter = new BlockCounter(MewUtils.config().max_iterations);
        this.enabledMaterials = EnumSet.copyOf(MewUtils.config().enabled_blocks);
        this.enabledWorlds = new HashSet<>(MewUtils.config().enabled_worlds);
    }

    @Override
    protected void enable() {

        // register listener
        registerListener(new BlockListener(this));

        // register command
        registerCommand(registry -> registry
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("toggle")
            .senderType(Player.class)
            .handler(commandContext -> {
                Player player = (Player) commandContext.getSender();
                if (isSubscriber(player.getUniqueId())) {
                    toggleSubscription(player.getUniqueId());
                    MewUtils.translations().of("found_ores.toggle-broadcast-off").send(player);
                } else {
                    toggleSubscription(player.getUniqueId());
                    MewUtils.translations().of("found_ores.toggle-broadcast-on").send(player);
                }
            })
        );
    }

    public BlockCounter getBlockCounter() {
        return this.blockCounter;
    }

    public boolean isSubscriber(UUID player) {
        return Metadata.provideForPlayer(player).getOrDefault(BROADCAST_TOGGLE_KEY, true);
    }

    public void toggleSubscription(UUID player) {
        Metadata.provideForPlayer(player).put(BROADCAST_TOGGLE_KEY, !isSubscriber(player));
    }

    public boolean shouldAnnounce(Block block) {
        return this.enabledMaterials.contains(block.getType()) && this.enabledWorlds.contains(block.getWorld().getName()) && !this.blockCounter.isDiscovered(block.getLocation());
    }

    @Override public String getId() {
        return "oreannouncer";
    }

    @Override public boolean canEnable() {
        return true;
    }

}
