package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.magicutils.events.MagicTimeEvent;
import co.mcsky.moeutils.magicutils.listeners.MagicTimeListener;
import co.mcsky.moeutils.utilities.CooldownManager;
import co.mcsky.moeutils.utilities.TimeConverter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MagicTime extends MagicBase {

    private final UUID COOLDOWN_KEY;
    private String lastPlayer = null;

    public MagicTime(MoeUtils plugin) {
        super(plugin, plugin.config.MAGICTIME_COOLDOWN);
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
        return checkBalance(player, plugin.config.MAGICTIME_COST);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEY);
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, plugin.config.MAGICTIME_COST);
    }

    public void use() {
        CooldownManager.use(COOLDOWN_KEY);
    }

    public void futureBroadcast(String timeName) {
        String prefix = plugin.getMessage(null, "magictime.prefix");
        String message = plugin.getMessage(null, "magictime.ended", "time", timeName);
        plugin.getServer()
              .getScheduler()
              .runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(prefix + message), TimeConverter.toTick(COOLDOWN_DURATION));
    }

    public void broadcast(String timeName) {
        String prefix = plugin.getMessage(null, "magictime.prefix");
        String message = plugin.getMessage(null, "magictime.changed", "time", timeName);
        plugin.getServer().broadcastMessage(prefix + message);
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
        CooldownManager.reset(COOLDOWN_KEY);
    }

}
