package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.magicutils.events.MagicWeatherEvent;
import co.mcsky.magicutils.listeners.MagicWeatherListener;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MagicWeather extends MagicBase {

    private final Map<String, UUID> COOLDOWN_KEYS;
    private final Map<String, String> lastPlayers;

    public MagicWeather(MoeUtils moe) {
        super(moe, moe.magicWeatherCfg.cooldown);
        COOLDOWN_KEYS = new HashMap<>() {{
            moe.getServer().getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
        }};
        lastPlayers = new HashMap<>();
        new MagicWeatherListener(this);
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

    public boolean checkBalance(Player player) {
        return checkBalance(player, moe.magicWeatherCfg.cost);
    }

    public boolean checkCooldown(Player player) {
        return checkCooldown(player, COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, moe.magicWeatherCfg.cost);
    }

    public void use(Player player) {
        CooldownUtil.use(COOLDOWN_KEYS.get(player.getWorld().getName()));
    }

    public void futureBroadcast(String weatherName, String worldName) {
        moe.getServer().getScheduler().runTaskLaterAsynchronously(moe, () -> moe.getServer().broadcastMessage(lm.magicweather_prefix + String.format(lm.magictime_ended, weatherName, worldName)), TimeConverter.toTick(COOLDOWN_DURATION));
    }

    public void broadcast(String weatherName, String worldName) {
        moe.getServer().broadcastMessage(lm.magicweather_prefix + String.format(lm.magicweather_changed, worldName, weatherName));
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
