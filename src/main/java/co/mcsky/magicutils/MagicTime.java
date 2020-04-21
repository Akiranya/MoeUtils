package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.magicutils.events.MagicTimeEvent;
import co.mcsky.magicutils.listeners.MagicTimeListener;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MagicTime extends MagicBase {

    private final UUID COOLDOWN_KEY;
    private String lastPlayer = null;

    public MagicTime(MoeUtils moe) {
        super(moe, moe.magicTimeCfg.cooldown);
        COOLDOWN_KEY = UUID.randomUUID();
        new MagicTimeListener(this);
    }

    public void call(TimeOption time, Player player) {
        MagicTimeEvent event = new MagicTimeEvent(player, time);
        moe.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            // By design, we set time for all worlds so that time between worlds are synchronized.
            moe.getServer().getWorlds().forEach(time::set);
            lastPlayer = player.getName();
        }
    }

    public boolean checkBalance(Player player) {
        return checkBalance(player, moe.magicWeatherCfg.cost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEY);
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, moe.magicTimeCfg.cost);
    }

    public void use() {
        CooldownUtil.use(COOLDOWN_KEY);
    }

    public void futureBroadcast(String timeName) {
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(lm.magictime_prefix + String.format(lm.magictime_ended, timeName)), TimeConverter.toTick(COOLDOWN_DURATION));
    }

    public void broadcast(String timeName) {
        moe.getServer().broadcastMessage(lm.magictime_prefix + String.format(lm.magictime_changed, timeName));
    }

    /**
     * @return The name of the last player who used magic weather.
     */
    public String getLastPlayer() {
        return lastPlayer;
    }

    /**
     * Reset the cooldown of magic time instance.
     */
    public void resetCooldown() {
        CooldownUtil.reset(COOLDOWN_KEY);
    }

}
