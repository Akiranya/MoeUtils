package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.magicutils.events.MagicWeatherEvent;
import co.mcsky.magicutils.listeners.MagicWeatherListener;
import co.mcsky.utilities.CooldownUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MagicWeather extends MagicBase {

    public final Map<String, UUID> COOLDOWN_KEYS;
    private final Map<String, String> lastPlayers;

    public MagicWeather(MoeUtils moe) {
        super(moe, moe.magicWeatherConfig.cooldown);
        COOLDOWN_KEYS = new HashMap<>() {{
            moe.getServer().getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
        }};
        lastPlayers = new HashMap<>();
        new MagicWeatherListener(moe, this);
    }

    /**
     * @param weather The weather to set the world to
     * @param player  The player related to this event
     */
    public void call(WeatherOption weather, Player player) {
        MagicWeatherEvent event = new MagicWeatherEvent(player, weather);
        moe.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            weather.set(player.getWorld());
            lastPlayers.put(player.getWorld().getName(), player.getName());
        }
    }

    /**
     * @return The name of the last player who used magic weather.
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

    public void resetCooldown() {
        COOLDOWN_KEYS.values().forEach(CooldownUtil::reset);
    }

}
