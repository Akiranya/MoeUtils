package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.magicutils.events.MagicTimeEvent;
import co.mcsky.moeutils.magicutils.listeners.MagicTimeListener;
import co.mcsky.moeutils.utilities.CooldownUtil;
import co.mcsky.moeutils.utilities.TimeConverter;
import org.bukkit.entity.Player;

import java.util.UUID;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class MagicTime extends MagicBase {

    private final UUID COOLDOWN_KEY;
    private String lastPlayer = null;

    public MagicTime() {
        super(plugin.config.magictime_cooldown);
        COOLDOWN_KEY = UUID.randomUUID();
        new MagicTimeListener(this);
    }

    public void call(TimeOption time, Player player) {
        MagicTimeEvent event = new MagicTimeEvent(player, time);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            // By design, we set time for all worlds so that time between worlds are synchronized.
            plugin.getServer().getWorlds().forEach(time::set);
            lastPlayer = player.getName();
        }
    }

    public boolean checkBalance(Player player) {
        return checkBalance(player, plugin.config.magictime_cost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEY);
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, plugin.config.magictime_cost);
    }

    public void use() {
        CooldownUtil.use(COOLDOWN_KEY);
    }

    public void futureBroadcast(String timeName) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(lang.magictime_prefix + String.format(lang.magictime_ended, timeName)), TimeConverter.toTick(COOLDOWN_DURATION));
    }

    public void broadcast(String timeName) {
        plugin.getServer().broadcastMessage(lang.magictime_prefix + String.format(lang.magictime_changed, timeName));
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
