package cc.mewcraft.mewutils.module.packet_filter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ProtocolLibHook implements Terminable {

    private final PacketFilterModule module;
    private final ProtocolManager protocolManager;

    public ProtocolLibHook(final PacketFilterModule module) {
        this.module = module;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.protocolManager.removePacketListeners(module.getParentPlugin()); // avoid duplicate registrations

        if (!module.filteredEntityTypes.isEmpty()) {
            this.protocolManager.addPacketListener(new PacketAdapter(module.getParentPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    EntityType entityType = packet.getEntityTypeModifier().readSafely(0);
                    if (module.filteredEntityTypes.contains(entityType)) {
                        int entityId = packet.getIntegers().readSafely(0);
                        module.whitelistEntityIds.add(entityId);
                        // module.info("Add entity id " + entityId + " to whitelist");
                    }
                }
            });
        }

        this.protocolManager.addPacketListener(new PacketAdapter(module.getParentPlugin(), ListenerPriority.HIGHEST, module.blockedPacketTypes) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                Integer entityId = event.getPacket().getIntegers().readSafely(0);
                if (entityId != null && module.whitelistEntityIds.contains(entityId))
                    return;
                if (module.afkPlayers.contains(player.getUniqueId()))
                    event.setCancelled(true);
            }
        });
    }

    @Override public void close() {
        this.protocolManager.removePacketListeners(this.module.getParentPlugin());
    }

}
