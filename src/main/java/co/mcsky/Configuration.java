package co.mcsky;

import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Configuration {

    // MobArena Addon
    public final boolean isMobArenaEnable;
    public final Set<EntityType> whiteList;

    // Safe Portal
    public final boolean isSafePortalEnable;
    public final String SafePortalMessage;

    public Configuration(MoeUtils plugin) {
        // MobArena Addon Initialization
        isMobArenaEnable = plugin.getConfig().getBoolean("mobarena-addon.enable");
        List<String> entityTypes = plugin.getConfig().getStringList("mobarena-addon.whitelist");
        whiteList = entityTypes.stream().map(EntityType::valueOf).collect(Collectors.toSet());

        // Safe Portal Initialization
        isSafePortalEnable = plugin.getConfig().getBoolean("safe-portal.enable");
        SafePortalMessage = plugin.getConfig().getString("safe-portal.messages.cancelled");
    }
}
