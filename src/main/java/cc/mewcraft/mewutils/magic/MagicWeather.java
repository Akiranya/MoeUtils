package cc.mewcraft.mewutils.magic;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.magic.event.MagicWeatherEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.TerminableConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MagicWeather extends MagicBase {

    private final Map<String, UUID> cooldownKeys;
    private final Map<String, String> lastPlayers;

    public MagicWeather() {
        super(MewUtils.config().magic_weather_cooldown);
        cooldownKeys = Collections.unmodifiableMap(new HashMap<>() {{
            Bukkit.getWorlds().forEach(world -> put(world.getName(), UUID.randomUUID()));
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
            startCooldown(player);
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
        MewUtils.p.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            weather.set(player.getWorld());
            lastPlayers.put(player.getWorld().getName(), player.getName());
        }
    }

    public boolean checkBalance(Player player) {
        return checkBalance(player, MewUtils.config().magic_weather_cost);
    }

    public boolean checkCooldown(Player player) {
        return testSilently(player, cooldownKeys.get(player.getWorld().getName()));
    }

    public void startCooldown(Player player) {
        test(cooldownKeys.get(player.getWorld().getName()));
    }

    public void chargePlayer(Player player) {
        chargePlayer(player, MewUtils.config().magic_weather_cost);
    }

    public void futureBroadcast(String weatherName, String worldName) {
        String prefix = MewUtils.translations().of("magic_weather.prefix").plain();
        String message = MewUtils.translations().of("magic_weather.ended").replace("weather", weatherName).replace("world", worldName).plain();
        Schedulers.bukkit().runTaskLaterAsynchronously(MewUtils.p, () -> {
            Bukkit.getServer().sendMessage(Component.text(prefix + message));
        }, Ticks.from(cooldownAmount, TimeUnit.SECONDS));
    }

    public void broadcast(String weatherName, String worldName) {
        String prefix = MewUtils.translations().of("magic_weather.prefix").plain();
        String message = MewUtils.translations().of("magic_weather.changed").replace("world", worldName).replace("weather", weatherName).plain();
        Bukkit.broadcast(Component.text(prefix + message));
    }

    /**
     * Reset the cooldown of magic weather instance.
     */
    public void resetCooldown() {
        cooldownKeys.values().forEach(this::reset);
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
