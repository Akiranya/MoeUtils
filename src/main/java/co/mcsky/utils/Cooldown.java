package co.mcsky.utils;

/**
 * Carries the data about cooldown.
 */
public class Cooldown {
    /**
     * Represents if the cooldown is ready.
     */
    private boolean ready;
    /**
     * The duration the player has to wait before cooldown is ready. In second.
     */
    private int remaining;

    public Cooldown(boolean ready, int remaining) {
        this.ready = ready;
        this.remaining = remaining;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
