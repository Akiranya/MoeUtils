package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.magicutils.events.MagicWeatherEvent;
import co.mcsky.moeutils.magicutils.listeners.MagicWeatherListener;
import co.mcsky.moeutils.utilities.CooldownManager;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class MagicWeather extends MagicBase {

    public static int magicWeatherCost;

    private final Map<String, UUID> COOLDOWN_KEYS;
    private final Map<String, String> lastPlayers;

    public MagicWeather() {
        // Configuration values
        super(plugin.config.node("magicweather", "cooldown").getInt(600));
        magicWeatherCost = plugin.config.node("magicweather", "cost").getInt(50);

        // Internal vars
        COOLDOWN_KEYS = Collections.unmodifiableMap(new HashMap<>() {{
            plugin.getServer().getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
        }});
        lastPlayers = new HashMap<>();

        // Register the listener
        new MagicWeatherListener(this);
    }

    /**
     * @param weather The weather to set the world to
     * @param player  The player related to this event
     */
    public void call(WeatherOption weather, Player player) {
        MagicWeatherEvent event = new MagicWeatherEvent(player, weather);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            weather.set(player.getWorld());
            lastPlayers.put(player.getWorld().getName(), player.getName());
        }
    }

    public boolean checkBalance(Player player) {
        return checkBalance(player, magicWeatherCost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, magicWeatherCost);
    }

    public void use(Player player) {
        CooldownManager.use(COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void futureBroadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(null, "magicweather.prefix");
        String message = plugin.getMessage(null, "magictime.ended",
                                           "weather", weatherName, "world", worldName);
        plugin.getServer()
              .getScheduler()
              .runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(prefix + message), COOLDOWN_DURATION * 20L);
    }

    public void broadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(null, "magicweather.prefix");
        String message = plugin.getMessage(null, "magicweather.changed",
                                           "world", worldName, "weather", weatherName);
        plugin.getServer().broadcastMessage(prefix + message);
    }

    /**
     * Reset the cooldown of magic weather instance.
     */
    public void resetCooldown() {
        COOLDOWN_KEYS.values().forEach(CooldownManager::reset);
    }

    /**
     * @return The last player who used magic weather.
     */
    public String getLastPlayers() {
        StringBuilder sb = new StringBuilder();
        lastPlayers.forEach(((world, player) -> sb.append("[ ")
                                                  .append(world)
                                                  .append(": ")
                                                  .append(player)
                                                  .append(" ]")));
        return sb.toString();
    }

}
