package cc.mewcraft.mewutils.misc;

import cc.mewcraft.mewutils.MewUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.NotNull;

public class BetterPortals implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MewUtils.logModule("BetterPortals", MewUtils.config().better_portals_enabled))
            return;

        Events.subscribe(PlayerPortalEvent.class)
            .filter(e -> !e.getTo().getWorld().getWorldBorder().isInside(e.getTo()))
            .handler(e -> {
                e.setCancelled(true);
                Player player = e.getPlayer();
                MewUtils.translations().of("better_portals.cancelled").send(player);
                if (MewUtils.config().debug) {
                    MewUtils.translations().of("better_portals.debug").replace("player", player.getName()).send(player);
                }
            }).bindWith(consumer);
    }
}
