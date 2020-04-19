package co.mcsky.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerUtil {

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
        return System.currentTimeMillis() - timeMap.get(key);
    }

}
