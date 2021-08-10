package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class BetterExplosion implements TerminableModule {
    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("BetterExplosion", MoeUtils.plugin.config.better_explosion_enabled))
            return;

        final HashSet<String> worldSet = new HashSet<>(MoeUtils.plugin.config.better_explosion_worlds);

        // stop drop items from being destroyed by explosions
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntity() instanceof Item)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                .filter(e -> worldSet.contains(e.getEntity().getLocation().getWorld().getName()))
                .handler(e -> e.setCancelled(true))
                .bindWith(consumer);
    }
}
