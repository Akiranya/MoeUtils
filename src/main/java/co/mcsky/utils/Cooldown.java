package co.mcsky.utils;

import java.util.concurrent.TimeUnit;

/**
 * Carries the data about cooldown.
 */
public class Cooldown {
    /**
     * <p>Represents if the cooldown is ready.</p>
     * <p>Marks it as final in case reckless user changes it through reference.</p>
     */
    public final boolean ready;
    /**
     * <p>The duration the player has to wait before cooldown is ready. In second.</p>
     * <p>Marks it as final in case reckless user changes it through reference.</p>
     */
    public final int remaining;

    private Cooldown(boolean ready, int remaining) {
        this.ready = ready;
        this.remaining = remaining;
    }

    /**
     * @param now          In millisecond.
     * @param lastUsedTime In millisecond.
     * @param cooldown     In second. The duration player has to wait for.
     * @return <p>The data about cooldown which tells:</p>
     * <p>1. If the cooldown is ready.</p>
     * <p>2. The remaining time player has to wait before the cooldown is done.</p>
     */
    public static Cooldown calculate(long now, long lastUsedTime, int cooldown) {
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (diff <= cooldown) { // Note that cooldown is in second
            int remaining = (int) (cooldown - diff);
            return new Cooldown(false, remaining);
        } else {
            return new Cooldown(true, 0);
        }
    }

}
