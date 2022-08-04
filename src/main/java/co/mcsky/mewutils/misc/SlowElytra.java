package co.mcsky.mewutils.misc;

import co.mcsky.mewutils.MewUtils;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.Log;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SlowElytra implements TerminableModule {

    private final CooldownMap<UUID> cooldownMap;
    private Set<String> limitedWorlds;
    private Set<BoostMethod> limitedMethods;

    public SlowElytra() {
        cooldownMap = CooldownMap.create(Cooldown.of(MewUtils.config().slow_elytra_cooldown, TimeUnit.MILLISECONDS));
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        if (MewUtils.logStatus("SlowElytra", MewUtils.config().slow_elytra_enabled))
            return;

        // suppress FIREWORK boost
        Events.subscribe(PlayerElytraBoostEvent.class).handler(e -> {
            if (!isMethodLimited(BoostMethod.FIREWORK)) return;
            if (e.isCancelled()) return;
            if (underTPSThreshold()) {
                // halt any boost if tps low

                if (MewUtils.config().debug) {
                    Log.info("Elytra boost canceled (firework; TPS)");
                }
                e.setShouldConsume(false);
                e.setCancelled(true);
                return;
            }
            Player p = e.getPlayer();
            if (inLimitedWorld(p) && !cooldownMap.test(p.getUniqueId())) {
                // handle cooldown

                if (MewUtils.config().debug) {
                    Log.info("Elytra boost canceled (firework; cooldown)");
                    Log.info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                }
                e.setShouldConsume(false);
                e.setCancelled(true);
            }
        }).bindWith(consumer);

        // suppress BOW boost
        Events.subscribe(ProjectileLaunchEvent.class).handler(e -> {
            if (!isMethodLimited(BoostMethod.BOW)) return;
            if (e.isCancelled()) return;
            Projectile proj = e.getEntity();
            if (proj instanceof Arrow && proj.getShooter() instanceof Player p && p.isGliding() && inLimitedWorld(p)) {
                if (underTPSThreshold()) {
                    // halt any boost if tps low

                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled (projectile; TPS)");
                    }
                    e.setCancelled(true);
                    return;
                }
                if (!cooldownMap.test(p.getUniqueId())) {
                    // handle cooldown

                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled (projectile; cooldown)");
                        Log.info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                    }
                }
                e.setCancelled(true);
            }

        }).bindWith(consumer);

        // suppress TRIDENT boost
        Events.subscribe(PlayerRiptideEvent.class).handler(e -> {
            if (!isMethodLimited(BoostMethod.RIPTIDE)) return;
            Player p = e.getPlayer();
            if (p.isGliding() && inLimitedWorld(p)) {
                if (underTPSThreshold()) {
                    // halt any boost if tps low

                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled (trident; TPS)");
                    }
                    p.setVelocity(p.getVelocity().multiply(0));
                    return;
                }
                if (!cooldownMap.test(p.getUniqueId())) {
                    // handle cooldown

                    if (MewUtils.config().debug) {
                        Log.info("Elytra boost canceled (trident; cooldown)");
                        Log.info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                    }
                    Vector slowVelocity = p.getVelocity().multiply(MewUtils.config().slow_elytra_velocity_multiply);
                    Schedulers.sync().runLater(() -> p.setVelocity(slowVelocity), 1);
                }
            }
        }).bindWith(consumer);

    }

    private boolean isMethodLimited(BoostMethod method) {
        if (limitedMethods == null) {
            limitedMethods = EnumSet.copyOf(MewUtils.config().slow_elytra_methods.stream().map(BoostMethod::valueOf).toList());
        }
        return limitedMethods.contains(method);
    }

    private boolean inLimitedWorld(Player player) {
        if (limitedWorlds == null)
            limitedWorlds = new HashSet<>(MewUtils.config().slow_elytra_worlds);
        return limitedWorlds.contains(player.getWorld().getName());
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
