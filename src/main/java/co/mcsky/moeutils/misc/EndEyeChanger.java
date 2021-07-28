package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class EndEyeChanger implements TerminableModule {

    private final MoeConfig config;
    private final List<Location> gateLocations;
    private final Map<World, List<Location>> gateLocationMap;

    public EndEyeChanger() throws SerializationException {
        config = MoeUtils.plugin.config;
        gateLocations = config.node("gate-locations").getList(Location.class, new ArrayList<>());
        gateLocationMap = new HashMap<>();
        gateLocations.forEach(loc -> {
            if (gateLocationMap.containsKey(loc.getWorld())) {
                gateLocationMap.get(loc.getWorld()).add(loc);
            } else {
                gateLocationMap.put(loc.getWorld(), new ArrayList<>() {{
                    add(loc);
                }});
            }
        });
    }

    private static int compareLocation(Location l1, Location l2) {
        if (l1.distance(l2) < 0) {
            return -1;
        } else if (l1.distance(l2) == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(EntitySpawnEvent.class)
                .filter(e -> !gateLocations.isEmpty())
                .filter(e -> e.getEntityType() == EntityType.ENDER_SIGNAL)
                .handler(e -> {
                    EnderSignal es = (EnderSignal) e;
                    final World world = e.getLocation().getWorld();
                    if (gateLocationMap.containsKey(world)) {

                        Location closeLocation = Collections.min(gateLocationMap.get(world), EndEyeChanger::compareLocation);
                        es.setTargetLocation(closeLocation);
                        if (config.debug) {
                            MoeUtils.plugin.getLogger().info("End eye target location set");
                        }
                    } else {

                        if (config.debug) {
                            MoeUtils.plugin.getLogger().info("Current %s is not set".formatted(world.getName()));
                        }
                    }
                }).bindWith(consumer);

    }
}
