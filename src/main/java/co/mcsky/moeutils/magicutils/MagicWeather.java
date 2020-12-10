package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.magicutils.events.MagicWeatherEvent;
import co.mcsky.moeutils.magicutils.listeners.MagicWeatherListener;
import co.mcsky.moeutils.utilities.CooldownUtil;
import co.mcsky.moeutils.utilities.TimeConverter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MagicWeather extends MagicBase {

    private final Map<String, UUID> COOLDOWN_KEYS;
    private final Map<String, String> lastPlayers;

    public MagicWeather(MoeUtils plugin) {
        super(plugin, plugin.config.MAGICWEATHER_COOLDOWN);
        COOLDOWN_KEYS = Collections.unmodifiableMap(new HashMap<>() {{
            plugin.getServer().getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
        }});
        lastPlayers = new HashMap<>();
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
        return checkBalance(player, plugin.config.MAGICWEATHER_COST);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, plugin.config.MAGICWEATHER_COST);
    }

    public void use(Player player) {
        CooldownUtil.use(COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void futureBroadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(plugin.getServer().getConsoleSender(), "magicweather.prefix");
        String message = String.format(plugin.getMessage(plugin.getServer().getConsoleSender(), "magictime.ended"), weatherName, worldName);
        plugin.getServer()
              .getScheduler()
              .runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(prefix + message), TimeConverter.toTick(COOLDOWN_DURATION));
    }

    public void broadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(plugin.getServer().getConsoleSender(), "magicweather.prefix");
        String message = String.format(plugin.getMessage(plugin.getServer().getConsoleSender(), "magicweather.changed"), worldName, weatherName);
        plugin.getServer().broadcastMessage(prefix + message);
    }

    /**
     * Reset the cooldown of magic weather instance.
     */
    public void resetCooldown() {
        COOLDOWN_KEYS.values().forEach(CooldownUtil::reset);
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
