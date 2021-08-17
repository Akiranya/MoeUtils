package co.mcsky.moeutils.data;

import java.util.*;

public class FoundOresData {

    // UUIDs of players that want to receive the broadcast
    private Set<UUID> listeners;

    public FoundOresData() {
        listeners = new HashSet<>();
    }

    public List<UUID> getReceiverList() {
        return new ArrayList<>(listeners);
    }

    public Set<UUID> getListeners() {
        return listeners;
    }

    public void setListeners(Set<UUID> listeners) {
        this.listeners = listeners;
    }

    public void setListeners(List<UUID> listeners) {
        this.listeners = new HashSet<>(listeners);
    }

}
