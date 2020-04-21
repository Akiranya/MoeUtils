package co.mcsky.magicutils.listeners;

import co.mcsky.MoeUtils;
import co.mcsky.LanguageManager;
import co.mcsky.magicutils.MagicBase;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.events.MagicWeatherEvent;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

/**
 * Responsible for checking precondition of magic weather call.
 */
public class MagicWeatherListener extends MagicBase implements Listener {

    private final Map<String, UUID> COOLDOWN_KEYS;
    private final LanguageManager lm;

    public MagicWeatherListener(MoeUtils moe, MagicWeather magic) {
        super(moe, moe.magicWeatherCfg.cooldown);
        this.COOLDOWN_KEYS = magic.COOLDOWN_KEYS;
        this.lm = moe.languageManager;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    @EventHandler
    public void onMagicWeatherChange(MagicWeatherEvent e) {
        Player player = e.getPlayer();
        String worldName = e.getWorld().getName();
        if (!(checkCooldown(player, COOLDOWN_KEYS.get(worldName)) && checkBalance(player, moe.magicWeatherCfg.cost))) {
            // If player does not meet the precondition, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        CooldownUtil.use(COOLDOWN_KEYS.get(worldName));
        chargePlayer(player, moe.magicWeatherCfg.cost);
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(lm.magicweather_prefix + String.format(lm.magictime_ended, e.getWeather().customName(lm), worldName)), TimeConverter.toTick(COOLDOWN_DURATION));
        moe.getServer().broadcastMessage(lm.magicweather_prefix + String.format(lm.magicweather_changed, worldName, e.getWeather().customName(lm)));
    }

}
