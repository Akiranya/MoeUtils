package cc.mewcraft.mewutils.module.elytralimiter;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldown;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.util.Log;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.util.Vector;

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
            Log.debug("Elytra boost canceled (firework; TPS)");
            this.module.getLang().of("no-boost-when-tps-low").send(player);
            event.setShouldConsume(false);
            event.setCancelled(true);
            return;
        }

        // Handle cooldown
        var cooldown = this.module.getCooldownMap().get(player.getUniqueId());
        if (this.module.inRestrictedWorld(player) && !cooldown.test()) {
            event.setShouldConsume(false);
            event.setCancelled(true);
            Log.debug("Elytra boost canceled  " + player.getName() + " (firework; cooldown)");
            Log.debug("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
        }

        // Always show progressbar when boosting
        this.module.getProgressbarMessenger().show(
            player,
            () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
            () -> this.module.getLang().of("cooldown-progressbar.head").plain(),
            () -> this.module.getLang().of("cooldown-progressbar.tail")
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
                Log.debug("Elytra boost canceled (projectile; TPS)");
                this.module.getLang().of("no-boost-when-tps-low").send(player);
                event.setCancelled(true);
                return;
            }

            // Handle cooldown
            var cooldown = this.module.getCooldownMap().get(player.getUniqueId());
            if (!cooldown.test()) {
                event.setCancelled(true);
                Log.debug("Elytra boost canceled " + player.getName() + " (projectile; cooldown)");
                Log.debug("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
            }

            // Always show progressbar when boosting
            this.module.getProgressbarMessenger().show(
                player,
                () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                () -> this.module.getLang().of("cooldown-progressbar.head").plain(),
                () -> this.module.getLang().of("cooldown-progressbar.tail")
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
                Log.debug("Elytra boost canceled " + player.getName() + " (trident; TPS)");
                this.module.getLang().of("no-boost-when-tps-low").send(player);
                player.setVelocity(player.getVelocity().multiply(0));
                return;
            }

            // Handle cooldown
            ChargeBasedCooldown cooldown = this.module.getCooldownMap().get(player.getUniqueId());
            if (!cooldown.test()) {
                Vector slow = player.getVelocity().multiply(this.module.getVelocityMultiply());
                Schedulers.sync().runLater(() -> player.setVelocity(slow), 1);

                Log.debug("Elytra boost canceled for " + player.getName() + " (trident; cooldown)");
                Log.debug("Cooldown remaining: " + cooldown.remainingMillisFull() + "ms");
            }

            // Always show progressbar when boosting
            this.module.getProgressbarMessenger().show(
                player,
                () -> (float) cooldown.elapsedOne() / cooldown.getBaseTimeout(),
                () -> this.module.getLang().of("slow_elytra.cooldown-progressbar.head").plain(),
                () -> this.module.getLang().of("slow_elytra.cooldown-progressbar.tail")
                    .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                    .replace("amount", cooldown.getAvailable())
                    .plain()
            );
        }
    }

}
