package co.mcsky.mewutils.misc;

import co.mcsky.mewutils.MewUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.Log;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.NotNull;

public class BetterPortals implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MewUtils.logStatus("BetterPortals", MewUtils.config().better_portals_enabled))
            return;

        Events.subscribe(PlayerPortalEvent.class)
                .filter(e -> !e.getTo().getWorld().getWorldBorder().isInside(e.getTo()))
                .handler(e -> {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(MewUtils.text("better-portals.cancelled"));
                    if (MewUtils.config().debug) {
                        Log.info(MewUtils.text("better-portals.debug", "player", e.getPlayer().getName()));
                    }
                }).bindWith(consumer);
    }
}
