package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.magicutils.events.MagicTimeEvent;
import co.mcsky.moeutils.magicutils.listeners.MagicTimeListener;
import co.mcsky.moeutils.utilities.CooldownManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class MagicTime extends MagicBase {

    public static int magicTimeCost;

    private final UUID COOLDOWN_KEY;
    private String lastPlayer = null;

    public MagicTime() {
        // Configuration values
        super(MoeUtils.plugin.config.node("magictime", "cooldown").getInt(600));
        magicTimeCost = plugin.config.node("magictime", "cost").getInt(50);

        // Internal vars
        COOLDOWN_KEY = UUID.randomUUID();

        // Register listener
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
        return checkBalance(player, magicTimeCost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEY);
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, magicTimeCost);
    }

    public void use() {
        CooldownManager.use(COOLDOWN_KEY);
    }

    public void futureBroadcast(String timeName) {
        String prefix = plugin.getMessage(null, "magictime.prefix");
        String message = plugin.getMessage(null, "magictime.ended", "time", timeName);
        plugin.getServer()
              .getScheduler()
              .runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(prefix + message), COOLDOWN_DURATION * 20L);
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
