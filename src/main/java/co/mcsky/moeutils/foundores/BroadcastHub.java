package co.mcsky.moeutils.foundores;

import co.mcsky.moeutils.MoeUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a channel to receive broadcast.
 * <p>
 * Any player who wants to receive broadcast messages from this channel must be
 * within the {@link #listeners}.
 */
public class BroadcastHub {

    private final Set<Player> listeners;

    public BroadcastHub() {
        this.listeners = new HashSet<>();
    }

    public void broadcast(Component component) {
        for (Player player : listeners) {
            player.sendMessage(component);
        }
    }

    public boolean contains(Player player) {
        return listeners.contains(player);
    }

    public void removeListener(Player player) {
        listeners.remove(player);
    }

    public void addListener(Player player) {
        listeners.add(player);
    }

    public void addAllListeners(Collection<Player> players) {
        listeners.addAll(players);
    }

}
