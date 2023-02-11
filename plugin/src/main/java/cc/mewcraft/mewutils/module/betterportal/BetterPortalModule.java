package cc.mewcraft.mewutils.module.betterportal;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;

public class BetterPortalModule extends ModuleBase implements AutoCloseableListener {

    @Inject
    public BetterPortalModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() {
        registerListener(this);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (event.getTo().getWorld().getWorldBorder().isInside(event.getTo()))
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        MewUtils.translations().of("better_portals.cancelled").send(player);
        if (MewUtils.config().debug) {
            MewUtils.translations().of("better_portals.debug").replace("player", player.getName()).send(player);
        }
    }

    @Override public String getId() {
        return "betterportals";
    }

    @Override public boolean canEnable() {
        return true;
    }

}
