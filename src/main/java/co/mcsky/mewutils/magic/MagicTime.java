package co.mcsky.mewutils.magic;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.magic.events.MagicTimeEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MagicTime extends MagicBase {

    private final UUID cooldownKey;
    private String lastPlayer;

    public MagicTime() {
        super(MewUtils.config().magic_time_cooldown);
        cooldownKey = UUID.randomUUID();
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(MagicTimeEvent.class).handler(e -> {
            Player player = e.getPlayer();
            if (!(checkCooldown(player) && checkBalance(player))) {
                e.setCancelled(true);
                return;
            }
            startCooldown();
            chargePlayer(player);
            broadcast(e.getTime().customName());
            futureBroadcast(e.getTime().customName());
        }).bindWith(consumer);
    }

    public void call(TimeOption time, Player player) {
        MagicTimeEvent event = new MagicTimeEvent(player, time);
        MewUtils.p.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            // set time for all worlds so that time between worlds are synchronized
            Bukkit.getWorlds().forEach(time::set);
            lastPlayer = player.getName();
        }
    }

    public boolean checkBalance(Player player) {
        return checkBalance(player, MewUtils.config().magic_time_cost);
    }

    public boolean checkCooldown(Player player) {
        return testSilently(player, cooldownKey);
    }

    public void startCooldown() {
        test(cooldownKey);
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, MewUtils.config().magic_time_cost);
    }

    public void futureBroadcast(String timeName) {
        String prefix = MewUtils.text("magic-time.prefix");
        String message = MewUtils.text("magic-time.ended", "time", timeName);
        Schedulers.bukkit().runTaskLaterAsynchronously(MewUtils.p, () -> {
            Bukkit.broadcast(Component.text(prefix + message));
        }, Ticks.from(cooldownAmount, TimeUnit.SECONDS));
    }

    public void broadcast(String timeName) {
        String prefix = MewUtils.text("magic-time.prefix");
        String message = MewUtils.text("magic-time.changed", "time", timeName);
        Bukkit.broadcast(Component.text(prefix + message));
    }

    /**
     * @return The name of the last player who used magic weather.
     */
    public String getLastPlayer() {
        return lastPlayer;
    }

    /**
     * Reset the cooldown of magic time instance.
     */
    public void resetCooldown() {
        reset(cooldownKey);
    }
}
