package co.mcsky.magicutils.listeners;

import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicBase;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.events.MagicTimeEvent;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Responsible for checking precondition of magic time call.
 */
public class MagicTimeListener extends MagicBase implements Listener {

    private final UUID COOLDOWN_KEY;

    public MagicTimeListener(MoeUtils moe, MagicTime magic) {
        super(moe, moe.magicTimeConfig.cooldown);
        this.COOLDOWN_KEY = magic.COOLDOWN_KEY;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    @EventHandler
    public void onMagicTimeChange(MagicTimeEvent e) {
        Player player = e.getPlayer();
        if (!(checkCooldown(player, COOLDOWN_KEY) && checkBalance(player, moe.magicTimeConfig.cost))) {
            // If player does not meet the requirement, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        chargePlayer(player, moe.magicTimeConfig.cost);
        CooldownUtil.use(COOLDOWN_KEY);
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(String.format(moe.magicTimeConfig.msg_ended, e.getTime().customName(moe))), TimeConverter.toTick(COOLDOWN_DURATION));
        moe.getServer().broadcastMessage(String.format(moe.magicTimeConfig.msg_changed, e.getTime().customName(moe)));
    }

}
