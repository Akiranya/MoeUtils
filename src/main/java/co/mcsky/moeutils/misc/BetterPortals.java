package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.NotNull;

public class BetterPortals implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.report("BetterPortals", MoeUtils.config().better_portals_enabled))
            return;

        Events.subscribe(PlayerPortalEvent.class)
                .filter(e -> !e.getTo().getWorld().getWorldBorder().isInside(e.getTo()))
                .handler(e -> {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(MoeUtils.text("better-portals.cancelled"));
                    if (MoeUtils.config().debug) {
                        MoeUtils.logger().info(MoeUtils.text("better-portals.debug", "player", e.getPlayer().getName()));
                    }
                }).bindWith(consumer);
    }
}
