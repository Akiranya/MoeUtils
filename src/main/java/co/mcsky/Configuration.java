package co.mcsky;

import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a singleton class, i.e. there is only one instance existing at the same time.<br>
 * All configurations should be listed here as members of this class.
 */
public class Configuration {

    private static Configuration configuration = null;

    // MobArena Addon
    public boolean isMobArenaEnable;
    public Set<EntityType> whiteList;

    // Safe Portal
    public boolean isSafePortalEnable;
    public String SafePortalMessage;

    // Global
    public String globalReloaded;

    private Configuration(MoeUtils plugin) {
        loadFile(plugin);
    }

    public void loadFile(MoeUtils plugin) {
        plugin.reloadConfig();

        // MobArena Addon initialization
        isMobArenaEnable = plugin.getConfig().getBoolean("mobarena-addon.enable");
        List<String> entityTypes = plugin.getConfig().getStringList("mobarena-addon.whitelist");
        whiteList = entityTypes.stream()
                .map(e -> EntityType.valueOf(e.toUpperCase()))
                .collect(Collectors.toSet());
        // Print out whiteList for double-checking
        whiteList.forEach(e -> plugin.getLogger().info(e.name()));

        // Safe Portal initialization
        isSafePortalEnable = plugin.getConfig().getBoolean("safe-portal.enable");
        SafePortalMessage = plugin.getConfig().getString("safe-portal.messages.cancelled");

        // Global initialization
        globalReloaded = plugin.getConfig().getString("global.messages.reloaded");
    }

    public static Configuration getInstance(MoeUtils plugin) {
        if (configuration == null) {
            configuration = new Configuration(plugin);
        }
        return configuration;
    }
}
