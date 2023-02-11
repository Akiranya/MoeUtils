package cc.mewcraft.mewutils.module.elytralimiter;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldown;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRiptideEvent;

import java.util.concurrent.TimeUnit;

public class ElytraBoostListener implements AutoCloseableListener {

    private final ElytraLimiterModule module;

    public ElytraBoostListener(final ElytraLimiterModule module) {
        this.module = module;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBoost(PlayerElytraBoostEvent event) {
        if (this.module.isBoostAllowed(BoostMethod.FIREWORK))
            return;

        Player player = event.getPlayer();

        // Halt any boost if tps low
        if (this.module.underTPSThreshold()) {
            if (MewUtils.config().debug) {
                MewUtils.log("Elytra boost canceled (firework; TPS)");
            }
            MewUtils.translations().of("slow_elytra.no-boost-when-tps-low").send(player);
            event.setShouldConsume(false);
            event.setCancelled(true);
            return;
        }

        // Handle cooldown
        var cooldown = this.module.getCooldownMap().get(player.getUniqueId());
        if (this.module.inRestrictedWorld(player) && !cooldown.test()) {
            event.setShouldConsume(false);
            event.setCancelled(true);
            if (MewUtils.config().debug) {
                MewUtils.log("Elytra boost canceled  " + player.getName() + " (firework; cooldown)");
                MewUtils.log("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
            }
        }

        // Always show progressbar when boosting
        this.module.getProgressbarMessenger().show(
            player,
            () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
            () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.head").plain(),
            () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.tail")
                .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                .replace("amount", cooldown.getAvailable())
                .plain()
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (this.module.isBoostAllowed(BoostMethod.BOW))
            return;

        if (event.getHitEntity() instanceof Player player &&
            event.getEntity() instanceof Arrow arrow &&
            arrow.getShooter() instanceof Player &&
            player.isGliding() &&
            this.module.inRestrictedWorld(player)
        ) {

            // Halt any boost if tps low
            if (this.module.underTPSThreshold()) {
                // halt any boost if tps low

                if (MewUtils.config().debug) {
                    MewUtils.log("Elytra boost canceled (projectile; TPS)");
                }
                MewUtils.translations().of("slow_elytra.no-boost-when-tps-low").send(player);
                event.setCancelled(true);
                return;
            }

            // Handle cooldown
            var cooldown = this.module.getCooldownMap().get(player.getUniqueId());
            if (!cooldown.test()) {
                event.setCancelled(true);
                if (MewUtils.config().debug) {
                    MewUtils.log("Elytra boost canceled " + player.getName() + " (projectile; cooldown)");
                    MewUtils.log("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
                }
            }

            // Always show progressbar when boosting
            this.module.getProgressbarMessenger().show(
                player,
                () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.head").plain(),
                () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.tail")
                    .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                    .replace("amount", cooldown.getAvailable())
                    .plain()
            );
        }
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent e) {
        if (this.module.isBoostAllowed(BoostMethod.RIPTIDE))
            return;

        Player player = e.getPlayer();
        if (player.isGliding() && this.module.inRestrictedWorld(player)) {

            // Halt any boost if tps low
            if (this.module.underTPSThreshold()) {

                if (MewUtils.config().debug) {
                    MewUtils.log("Elytra boost canceled " + player.getName() + " (trident; TPS)");
                }
                MewUtils.translations().of("slow_elytra.no-boost-when-tps-low").send(player);
                player.setVelocity(player.getVelocity().multiply(0));
                return;
            }

            // Handle cooldown
            ChargeBasedCooldown cooldown = this.module.getCooldownMap().get(player.getUniqueId());
            if (!cooldown.test()) {
                var slow = player.getVelocity().multiply(MewUtils.config().slow_elytra_velocity_multiply);
                Schedulers.sync().runLater(() -> player.setVelocity(slow), 1);
                if (MewUtils.config().debug) {
                    MewUtils.log("Elytra boost canceled for " + player.getName() + " (trident; cooldown)");
                    MewUtils.log("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
                }
            }

            // Always show progressbar when boosting
            this.module.getProgressbarMessenger().show(
                player,
                () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.head").plain(),
                () -> MewUtils.translations().of("slow_elytra.cooldown-progressbar.tail")
                    .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                    .replace("amount", cooldown.getAvailable())
                    .plain()
            );
        }
    }

}
