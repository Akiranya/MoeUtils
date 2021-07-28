package co.mcsky.moeutils.magic.listeners;

import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.events.MagicTimeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static co.mcsky.moeutils.MoeUtils.plugin;

/**
 * Responsible for checking precondition of magic time call.
 */
public class MagicTimeListener implements Listener {

    private final MagicTime magic;

    public MagicTimeListener(MagicTime magic) {
        this.magic = magic;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMagicTimeChange(MagicTimeEvent e) {
        Player player = e.getPlayer();
        if (!(magic.checkCooldown(player) && magic.checkBalance(player))) {
            // If player does not meet the precondition, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        magic.use();
        magic.chargePlayer(player);
        magic.broadcast(e.getTime().customName());
        magic.futureBroadcast(e.getTime().customName());
    }

}
