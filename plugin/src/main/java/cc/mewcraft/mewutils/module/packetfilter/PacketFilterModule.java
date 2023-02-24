package cc.mewcraft.mewutils.module.packetfilter;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.comphenix.protocol.PacketType;
import com.google.inject.Inject;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketFilterModule extends ModuleBase implements AutoCloseableListener {

    Set<UUID> afkPlayers; // players who are afk-ing
    Set<Integer> whitelistEntityIds; // ids of entity whose packets should not be blocked
    EnumSet<EntityType> filteredEntityTypes; // allowed entity packet types
    Set<PacketType> blockedPacketTypes; // packet types to be blocked // TODO don't import ProtocolLib classes here

    @Inject
    public PacketFilterModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void load() throws Exception {
        // Initialize member fields
        this.afkPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.whitelistEntityIds = Collections.newSetFromMap(new WeakHashMap<>());
        this.filteredEntityTypes = EnumSet.noneOf(EntityType.class);
        this.blockedPacketTypes = new HashSet<>();

        // Read the config values
        getConfigNode().node("whitelistEntities").getList(String.class, List.of())
            .stream()
            .map(EntityType::valueOf)
            .forEach(entity -> {
                this.filteredEntityTypes.add(entity);
                info("Whitelist entity added: " + entity);
            });
        getConfigNode().node("blockedPackets").getList(String.class, List.of())
            .stream()
            .flatMap(name -> PacketType.fromName(name).stream())
            .forEach(type -> {
                info("Added blocked packet type: " + type);
                this.blockedPacketTypes.add(type);
            });
    }

    @Override protected void postLoad() throws Exception {
        registerListener(new EssentialsHook(this));
        new ProtocolLibHook(this).bindWith(this);
    }

    @Override public boolean checkRequirement() {
        return isPluginPresent("Essentials") && isPluginPresent("ProtocolLib");
    }

}
