package co.mcsky.moeutils.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class BetterPortals implements Listener {

    public static boolean enable;
    public static boolean debug;

    public BetterPortals() {
        // Configuration values
        enable = plugin.config.node("betterportals", "enable").getBoolean();
        debug = plugin.config.node("betterportals", "debug").getBoolean();

        if (enable) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("BetterPortals is enabled.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerPortalEvent e) {
        // Due to the location transformation between Overworld and Nether,
        // players might be teleported to a location outside of target world (e.g. Nether).

        // Also, if that happens, players will be killed due to being outside of the world border,
        // meaning that their death drops will be outside of the world border too, which is a pain to players.

        // To avoid it, we see if the target location is outside of the target world border,
        // and cancel the PlayerPortalEvent when it is.
        if (!e.getTo().getWorld().getWorldBorder().isInside(e.getTo())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.getMessage(e.getPlayer(), "betterportals.cancelled"));
            if (debug) {
                plugin.getLogger().info(plugin.getMessage(null, "betterportals.debug", "player", e.getPlayer().getName()));
            }
        }
    }

}
