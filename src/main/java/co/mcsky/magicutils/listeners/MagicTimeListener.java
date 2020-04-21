package co.mcsky.magicutils.listeners;

import co.mcsky.MoeUtils;
import co.mcsky.LanguageManager;
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
    private final LanguageManager lm;

    public MagicTimeListener(MoeUtils moe, MagicTime magic) {
        super(moe, moe.magicTimeCfg.cooldown);
        this.COOLDOWN_KEY = magic.COOLDOWN_KEY;
        this.lm = moe.languageManager;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    @EventHandler
    public void onMagicTimeChange(MagicTimeEvent e) {
        Player player = e.getPlayer();
        if (!(checkCooldown(player, COOLDOWN_KEY) && checkBalance(player, moe.magicTimeCfg.cost))) {
            // If player does not meet the precondition, we cancel the magic event.
            e.setCancelled(true);
            return;
        }
        CooldownUtil.use(COOLDOWN_KEY);
        chargePlayer(player, moe.magicTimeCfg.cost);
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(lm.magictime_prefix + String.format(lm.magictime_ended, e.getTime().customName(lm))), TimeConverter.toTick(COOLDOWN_DURATION));
        moe.getServer().broadcastMessage(lm.magictime_prefix + String.format(lm.magictime_changed, e.getTime().customName(lm)));
    }

}
