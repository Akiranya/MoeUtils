package co.mcsky.moeutils.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Timer {

    private static final Map<UUID, Long> timeMap = new HashMap<>();

    /**
     * @param key Starts the timer and binds the timer to the given UUID.
     */
    public static void start(UUID key) {
        timeMap.put(key, System.currentTimeMillis());
    }

    /**
     * @param key The UUID which bind to a starting timer.
     *
     * @return Elapsed time (in millisecond) since the timer started.
     */
    public static long end(UUID key) {
        long elapsed = 0;
        try {
            elapsed = System.currentTimeMillis() - timeMap.get(key);
        } catch (NullPointerException e) {
            System.err.println("[TimerUtil] The UUID does not exist, returning 0 instead.");
        }
        return elapsed;
    }

}
