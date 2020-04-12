package co.mcsky.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeUtil {

    public static class Timer {

        private static Map<UUID, Long> timeMap = new HashMap<>();
        private String title;

        public void start(String className, UUID key) {
            this.title = className;
            timeMap.put(key, System.currentTimeMillis());
            System.out.println(title + ": timer start!");
        }

        public void end(UUID key) {
            long end = System.currentTimeMillis();
            System.out.println(title + ": elapsed " + (end - timeMap.get(key)) + " ms");
        }
    }

    public static int toTick(int seconds) {
        return seconds * 20;
    }
}
