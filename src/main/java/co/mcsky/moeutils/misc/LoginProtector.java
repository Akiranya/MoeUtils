package co.mcsky.moeutils.misc;

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

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!MoeUtils.logActiveStatus("LoginProtection", MoeUtils.plugin.config.login_protection_enabled)) return;

        Events.subscribe(PlayerJoinEvent.class).handler(e -> {
            final int duration = (int) Ticks.from(MoeUtils.plugin.config.login_protection_duration, TimeUnit.SECONDS);
            final int amplifier = MoeUtils.plugin.config.login_protection_amplifier;
            final PotionEffect effect = new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier);
            e.getPlayer().addPotionEffect(effect);
        }).bindWith(consumer);
    }
}
