package co.mcsky.mewutils.magic.events;

import co.mcsky.mewutils.magic.WeatherOption;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MagicWeatherEvent extends Event implements Cancellable {

    public static final HandlerList handlers = new HandlerList();
    private final World world;
    private final Player player;
    private final WeatherOption weather;
    private boolean cancelled;

    public MagicWeatherEvent(Player player, WeatherOption weather) {
        this.player = player;
        this.world = player.getWorld();
        this.weather = weather;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public WeatherOption getWeather() {
        return weather;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
