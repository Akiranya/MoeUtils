package co.mcsky.utils;

/**
 * Carries the data about cooldown.
 */
public class Cooldown {
    /**
     * Represents if the cooldown is ready.
     */
    public boolean ready;
    /**
     * The duration the player has to wait before cooldown is ready. In second.
     */
    public int remaining;

    public Cooldown(boolean ready, int remaining) {
        this.ready = ready;
        this.remaining = remaining;
    }

}
