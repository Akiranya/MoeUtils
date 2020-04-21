package co.mcsky.magicutils.listeners;

import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.events.MagicWeatherEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Responsible for checking precondition of magic weather call.
 */
public class MagicWeatherListener implements Listener {

    private final LanguageManager lm;
    private final MagicWeather magic;

    public MagicWeatherListener(MagicWeather magic) {
        this.magic = magic;
        this.lm = magic.lm;
        MoeUtils moe = magic.moe;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    @EventHandler
    public void onMagicWeatherChange(MagicWeatherEvent e) {
        Player player = e.getPlayer();
        String worldName = e.getWorld().getName();
        if (!(magic.checkCooldown(player) && magic.checkBalance(player))) {
            // If player does not meet the precondition, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        magic.use(player);
        magic.chargePlayer(player);
        magic.broadcast(e.getWeather().customName(lm), worldName);
        magic.futureBroadcast(e.getWeather().customName(lm), worldName);
    }

}
