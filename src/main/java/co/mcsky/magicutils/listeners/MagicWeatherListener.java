package co.mcsky.magicutils.listeners;

import co.mcsky.MoeUtils;
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

    public MagicWeatherListener(MoeUtils moe, MagicWeather magic) {
        super(moe, moe.magicWeatherConfig.cooldown);
        COOLDOWN_KEYS = magic.COOLDOWN_KEYS;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    @EventHandler
    public void onMagicWeatherChange(MagicWeatherEvent e) {
        Player player = e.getPlayer();
        String worldName = e.getWorld().getName();
        if (!(checkCooldown(player, COOLDOWN_KEYS.get(worldName)) && checkBalance(player, moe.magicWeatherConfig.cost))) {
            // If player does not meet the requirement, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        chargePlayer(player, moe.magicWeatherConfig.cost);
        CooldownUtil.use(COOLDOWN_KEYS.get(worldName));
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(String.format(moe.magicWeatherConfig.msg_ended, e.getWeather().customName(moe), worldName)), TimeConverter.toTick(COOLDOWN_DURATION));
        moe.getServer().broadcastMessage(String.format(moe.magicWeatherConfig.msg_changed, worldName, e.getWeather().customName(moe)));
    }

}
