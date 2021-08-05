package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EndEyeChanger implements TerminableModule {

    public EndEyeChanger() {
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("EndEyeChanger", MoeUtils.plugin.config.end_eye_changer_enabled)) return;

        // spawn an end signal entity if the world does not spawn
        Events.subscribe(PlayerInteractEvent.class)
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> e.getMaterial() == Material.ENDER_EYE)
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(e -> MoeUtils.plugin.getDatasource().containsEndEyeTargetWorld(e.getPlayer().getWorld().getName()))
                .handler(e -> {
                    final Player player = e.getPlayer();
                    final ItemStack hand = e.getItem();

                    // take one end eye from player
                    //noinspection ConstantConditions
                    final int handAmount = hand.getAmount();
                    int resultAmount = handAmount - 1;
                    if (resultAmount <= 0) {
                        e.getPlayer().getInventory().setItemInMainHand(null);
                    } else {
                        e.getPlayer().getInventory().setItemInMainHand(e.getItem().asQuantity(resultAmount));
                    }

                    player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_SIGNAL);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_LAUNCH, 1, 1);
                }).bindWith(consumer);

        // change the target of end signal
        Events.subscribe(EntitySpawnEvent.class)
                .filter(e -> e.getEntity() instanceof EnderSignal)
                .handler(e -> {
                    EnderSignal es = (EnderSignal) e.getEntity();
                    final World world = e.getEntity().getLocation().getWorld();
                    if (MoeUtils.plugin.getDatasource().containsEndEyeTargetWorld(world.getName())) {
                        Location closeLocation = nearestLocation(e.getEntity().getLocation(), MoeUtils.plugin.getDatasource().getEndEyeTargetLocationsByWorld(world.getName()));
                        es.setTargetLocation(closeLocation);
                        if (MoeUtils.plugin.config.debug) {
                            MoeUtils.plugin.getLogger().info("Found an end eye target location set");
                        }
                    } else {
                        if (MoeUtils.plugin.config.debug) {
                            MoeUtils.plugin.getLogger().info("%s's end eye target location is not set".formatted(world.getName()));
                        }
                    }
                }).bindWith(consumer);
    }

    private Location nearestLocation(Location base, List<Location> targets) {
        int minIndex = 0;
        double min = base.distance(targets.get(0));
        for (int i = 1; i < targets.size(); i++) {
            double tempMin = base.distance(targets.get(i));
            if (tempMin < min) {
                minIndex = i;
                min = tempMin;
            }
        }
        return targets.get(minIndex);
    }

}
