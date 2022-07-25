package co.mcsky.mewutils.misc;

import co.mcsky.mewutils.MewUtils;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SlowElytra implements TerminableModule {

    private final CooldownMap<UUID> cooldownMap;
    private Set<String> limitedWorlds;

    public SlowElytra() {
        cooldownMap = CooldownMap.create(Cooldown.of(MewUtils.config().slow_elytra_cooldown, TimeUnit.MILLISECONDS));
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        if (MewUtils.report("SlowElytra", MewUtils.config().slow_elytra_enabled))
            return;

        // suppress FIREWORK boost
        Events.subscribe(PlayerElytraBoostEvent.class).handler(e -> {

            if (e.isCancelled()) return;

            // halt any boost if tps low
            if (underTPSThreshold()) {
                MewUtils.logger().info("Elytra boost canceled (firework; TPS)");
                e.setShouldConsume(false);
                e.setCancelled(true);
                return;
            }

            // handle cooldown
            Player p = e.getPlayer();
            if (isInLimitWorld(p) && !cooldownMap.test(p.getUniqueId())) {
                MewUtils.logger().info("Elytra boost canceled (firework; cooldown)");
                MewUtils.logger().info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                e.setShouldConsume(false);
                e.setCancelled(true);
            }
        }).bindWith(consumer);

        // suppress PROJECTILE boost
        Events.subscribe(ProjectileLaunchEvent.class).handler(e -> {

            if (e.isCancelled()) return;

            // halt any boost if tps low
            if (underTPSThreshold()) {
                MewUtils.logger().info("Elytra boost canceled (projectile; TPS)");
                e.setCancelled(true);
                return;
            }

            // handle cooldown
            Projectile proj = e.getEntity();
            if (proj.getShooter() instanceof Player p && proj instanceof Arrow && p.isGliding() && isInLimitWorld(p) && !cooldownMap.test(p.getUniqueId())) {
                MewUtils.logger().info("Elytra boost canceled (projectile; cooldown)");
                MewUtils.logger().info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                e.setCancelled(true);
            }

        }).bindWith(consumer);

        // suppress TRIDENT boost
        Events.subscribe(PlayerRiptideEvent.class).handler(e -> {
            Player p = e.getPlayer();

            // halt any boost if tps low
            if (underTPSThreshold()) {
                MewUtils.logger().info("Elytra boost canceled (trident; TPS)");
                p.setVelocity(p.getVelocity().multiply(0));
                return;
            }

            if (p.isGliding() && isInLimitWorld(p) && !cooldownMap.test(p.getUniqueId())) {
                MewUtils.logger().info("Elytra boost canceled (trident; cooldown)");
                MewUtils.logger().info("Cooldown remaining: " + cooldownMap.remainingMillis(p.getUniqueId()) + "ms");
                Vector slowVel = p.getVelocity().multiply(MewUtils.config().slow_elytra_velocity_multiply);
                Schedulers.sync().runLater(() -> p.setVelocity(slowVel), 1);
            }

        }).bindWith(consumer);

    }

    private boolean isInLimitWorld(Player player) {
        if (limitedWorlds == null)
            limitedWorlds = new HashSet<>(MewUtils.config().slow_elytra_worlds);
        return limitedWorlds.contains(player.getWorld().getName());
    }

    private boolean underTPSThreshold() {
        return MewUtils.p.getServer().getTPS()[0] <= MewUtils.config().slow_elytra_tps_threshold;
    }

}
