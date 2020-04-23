package co.mcsky.moeutils.magicutils.events;

import co.mcsky.moeutils.magicutils.TimeOption;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MagicTimeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Player player;
    @Getter private final TimeOption time;
    private boolean cancelled;

    public MagicTimeEvent(Player player, TimeOption time) {
        this.player = player;
        this.time = time;
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
