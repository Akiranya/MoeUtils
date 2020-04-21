package co.mcsky.magicutils.listeners;

import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.events.MagicTimeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Responsible for checking precondition of magic time call.
 */
public class MagicTimeListener implements Listener {

    private final LanguageManager lm;
    private final MagicTime magic;

    public MagicTimeListener(MagicTime magic) {
        this.magic = magic;
        this.lm = magic.lm;
        MoeUtils moe = magic.moe;
        moe.getServer().getPluginManager().registerEvents(this, moe);
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
        magic.broadcast(e.getTime().customName(lm));
        magic.futureBroadcast(e.getTime().customName(lm));
    }

}
