package co.mcsky.moeutils.magic.listeners;

import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.magic.events.MagicWeatherEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static co.mcsky.moeutils.MoeUtils.plugin;

/**
 * Responsible for checking precondition of magic weather call.
 */
public class MagicWeatherListener implements Listener {

    private final MagicWeather magic;

    public MagicWeatherListener(MagicWeather magic) {
        this.magic = magic;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
        magic.broadcast(e.getWeather().customName(), worldName);
        magic.futureBroadcast(e.getWeather().customName(), worldName);
    }

}
