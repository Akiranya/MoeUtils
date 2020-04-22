package co.mcsky.misc;

import co.mcsky.LanguageRepository;
import co.mcsky.MoeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class BetterPortals implements Listener {

    private final MoeUtils moe;
    private final LanguageRepository lang;

    public BetterPortals(MoeUtils moe) {
        this.moe = moe;
        this.lang = moe.lang;
        if (moe.config.betterportals_enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("OptimizedNetherPortal is enabled.");
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
            e.getPlayer().sendMessage(lang.betterportals_cancelled);
            if (moe.config.betterportals_debug) {
                moe.getLogger().info(String.format(lang.betterportals_debug, e.getPlayer().getName()));
            }
        }
    }

}
