package co.mcsky.magicutils.events;

import co.mcsky.magicutils.WeatherOption;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MagicWeatherEvent extends Event implements Cancellable {

    public static final HandlerList handlers = new HandlerList();
    @Getter private final World world;
    @Getter private final Player player;
    @Getter private final WeatherOption weather;
    private boolean cancelled;

    public MagicWeatherEvent(Player player, WeatherOption weather) {
        this.player = player;
        this.world = player.getWorld();
        this.weather = weather;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull
    HandlerList getHandlers() {
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
