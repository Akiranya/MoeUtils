package cc.mewcraft.mewutils.misc;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewcore.progressbar.ProgressbarMessenger;
import cc.mewcraft.mewutils.MewUtils;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.Log;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SlowElytra implements TerminableModule {

    private final ChargeBasedCooldownMap<UUID> cooldownMap;
    private final Set<String> restrictedWorlds;
    private final Set<BoostMethod> restrictedBoost;
    private final ProgressbarMessenger progressbarMessenger;

    public SlowElytra() {
        cooldownMap = ChargeBasedCooldownMap.create(
                Cooldown.of(MewUtils.config().slow_elytra_cooldown, TimeUnit.MILLISECONDS),
                uuid -> MewUtils.config().slow_elytra_cooldown_charge
        );
        restrictedWorlds = new HashSet<>(MewUtils.config().slow_elytra_worlds);
        restrictedBoost = EnumSet.copyOf(MewUtils.config().slow_elytra_methods.stream().map(BoostMethod::valueOf).toList());
        progressbarMessenger = new ProgressbarMessenger(
                MewUtils.config().slow_elytra_bar_stay_time,
                ProgressbarGenerator.Builder.builder()
                        .left(MewUtils.text("slow-elytra.cooldown-progressbar.left"))
                        .full(MewUtils.text("slow-elytra.cooldown-progressbar.full"))
                        .empty(MewUtils.text("slow-elytra.cooldown-progressbar.empty"))
                        .right(MewUtils.text("slow-elytra.cooldown-progressbar.right"))
                        .width(MewUtils.config().slow_elytra_bar_width)
                        .build()
        );
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        if (MewUtils.logStatus("SlowElytra", MewUtils.config().slow_elytra_enabled))
            return;

        // suppress FIREWORK boost
        Events.subscribe(PlayerElytraBoostEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e -> {
                    if (!isBoostRestricted(BoostMethod.FIREWORK)) return;
                    var player = e.getPlayer();

                    // Halt any boost if tps low
                    if (underTPSThreshold()) {
                        if (MewUtils.config().debug) {
                            Log.info("Elytra boost canceled (firework; TPS)");
                        }
                        player.sendMessage(MewUtils.text("slow-elytra.no-boost-when-tps-low"));
                        e.setShouldConsume(false);
                        e.setCancelled(true);
                        return;
                    }

                    // Handle cooldown
                    var cooldown = cooldownMap.get(player.getUniqueId());
                    if (inRestrictedWorld(player) && !cooldown.test()) {
                        e.setShouldConsume(false);
                        e.setCancelled(true);
                        if (MewUtils.config().debug) {
                            Log.info("Elytra boost canceled  " + player.getName() + " (firework; cooldown)");
                            Log.info("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
                        }
                    }

                    // Always show progressbar when boosting
                    progressbarMessenger.show(
                            player,
                            () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                            () -> MewUtils.text("slow-elytra.cooldown-progressbar.head"),
                            () -> MewUtils.text("slow-elytra.cooldown-progressbar.tail", "remaining", String.valueOf(cooldown.remainingTime(TimeUnit.SECONDS)), "amount", String.valueOf(cooldown.getAvailable()))
                    );
                }).bindWith(consumer);

        // suppress BOW boost
        Events.subscribe(ProjectileHitEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(event -> {
                    if (!isBoostRestricted(BoostMethod.BOW)) return;
                    if (event.getHitEntity() instanceof Player player &&
                        event.getEntity() instanceof Arrow arrow &&
                        arrow.getShooter() instanceof Player &&
                        player.isGliding() &&
                        inRestrictedWorld(player)
                    ) {

                        // Halt any boost if tps low
                        if (underTPSThreshold()) {
                            // halt any boost if tps low

                            if (MewUtils.config().debug) {
                                Log.info("Elytra boost canceled (projectile; TPS)");
                            }
                            player.sendMessage(MewUtils.text("slow-elytra.no-boost-when-tps-low"));
                            event.setCancelled(true);
                            return;
                        }

                        // Handle cooldown
                        var cooldown = cooldownMap.get(player.getUniqueId());
                        if (!cooldown.test()) {
                            event.setCancelled(true);
                            if (MewUtils.config().debug) {
                                Log.info("Elytra boost canceled " + player.getName() + " (projectile; cooldown)");
                                Log.info("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
                            }
                        }

                        // Always show progressbar when boosting
                        progressbarMessenger.show(
                                player,
                                () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                                () -> MewUtils.text("slow-elytra.cooldown-progressbar.head"),
                                () -> MewUtils.text("slow-elytra.cooldown-progressbar.tail", "remaining", String.valueOf(cooldown.remainingTime(TimeUnit.SECONDS)), "amount", String.valueOf(cooldown.getAvailable()))
                        );
                    }
                }).bindWith(consumer);

        // suppress TRIDENT boost
        Events.subscribe(PlayerRiptideEvent.class).handler(e -> {
            if (!isBoostRestricted(BoostMethod.RIPTIDE)) return;
            var player = e.getPlayer();
            if (player.isGliding() && inRestrictedWorld(player)) {

                // Halt any boost if tps low
                if (underTPSThreshold()) {

                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled " + player.getName() + " (trident; TPS)");
                    }
                    player.sendMessage(MewUtils.text("slow-elytra.no-boost-when-tps-low"));
                    player.setVelocity(player.getVelocity().multiply(0));
                    return;
                }

                // Handle cooldown
                var cooldown = cooldownMap.get(player.getUniqueId());
                if (!cooldown.test()) {
                    var slow = player.getVelocity().multiply(MewUtils.config().slow_elytra_velocity_multiply);
                    Schedulers.sync().runLater(() -> player.setVelocity(slow), 1);
                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled for " + player.getName() + " (trident; cooldown)");
                        Log.info("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
                    }
                }

                // Always show progressbar when boosting
                progressbarMessenger.show(
                        player,
                        () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                        () -> MewUtils.text("slow-elytra.cooldown-progressbar.head"),
                        () -> MewUtils.text("slow-elytra.cooldown-progressbar.tail", "remaining", String.valueOf(cooldown.remainingTime(TimeUnit.SECONDS)), "amount", String.valueOf(cooldown.getAvailable()))
                );
            }
        }).bindWith(consumer);

    }

    private boolean isBoostRestricted(BoostMethod method) {
        return restrictedBoost.contains(method);
    }

    private boolean inRestrictedWorld(Player player) {
        return restrictedWorlds.contains(player.getWorld().getName());
    }

    private boolean underTPSThreshold() {
        return MewUtils.p.getServer().getTPS()[0] <= MewUtils.config().slow_elytra_tps_threshold;
    }

    public enum BoostMethod {
        FIREWORK,
        RIPTIDE,
        BOW,
    }

}
