package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginProtector implements TerminableModule {

    MoeConfig config;

    public LoginProtector() {
        this.config = MoeUtils.plugin.config;
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class).handler(e -> {
            int duration = (int) Ticks.from(config.node("login-protection", "duration-in-sec").getLong(15), TimeUnit.SECONDS);
            int amplifier = config.node("login-protection", "amplifier").getInt(4);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
        }).bindWith(consumer);
    }
}
