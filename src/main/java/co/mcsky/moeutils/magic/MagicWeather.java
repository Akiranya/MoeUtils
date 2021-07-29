package co.mcsky.moeutils.magic;

import co.mcsky.moeutils.magic.events.MagicWeatherEvent;
import co.mcsky.moeutils.util.CooldownManager;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class MagicWeather extends MagicBase {

    private final Map<String, UUID> COOLDOWN_KEYS;
    private final Map<String, String> lastPlayers;

    public MagicWeather() {
        // Configuration values
        super(plugin.config.magic_weather_cooldown);
        COOLDOWN_KEYS = Collections.unmodifiableMap(new HashMap<>() {{
            plugin.getServer().getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
        }});
        lastPlayers = new HashMap<>();
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(MagicWeatherEvent.class).handler(e -> {
            Player player = e.getPlayer();
            String worldName = e.getWorld().getName();
            if (!(checkCooldown(player) && checkBalance(player))) {
                e.setCancelled(true);
                return;
            }
            use(player);
            chargePlayer(player);
            broadcast(e.getWeather().customName(), worldName);
            futureBroadcast(e.getWeather().customName(), worldName);
        }).bindWith(consumer);
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
        return checkBalance(player, plugin.config.magic_weather_cost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, plugin.config.magic_weather_cost);
    }

    public void use(Player player) {
        CooldownManager.use(COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void futureBroadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(null, "magic-weather.prefix");
        String message = plugin.getMessage(null, "magic-weather.ended", "weather", weatherName, "world", worldName);
        Schedulers.bukkit().runTaskLaterAsynchronously(plugin, () -> plugin.getServer().broadcastMessage(prefix + message), Ticks.from(COOLDOWN_DURATION, TimeUnit.SECONDS));
    }

    public void broadcast(String weatherName, String worldName) {
        String prefix = plugin.getMessage(null, "magic-weather.prefix");
        String message = plugin.getMessage(null, "magic-weather.changed", "world", worldName, "weather", weatherName);
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
