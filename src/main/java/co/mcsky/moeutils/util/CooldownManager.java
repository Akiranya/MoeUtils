package co.mcsky.moeutils.util;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private static final Map<UUID, UserData> cooldownData = new HashMap<>();

    /**
     * Uses the cooldown of given user. In other words, this method updates the
     * last use time to {@code now} for given user.
     *
     * @param user The user who uses the cooldown
     */
    public static void use(UUID user) {
        Validate.notNull(cooldownData.get(user), "specific cooldown key doesn't exist.");
        cooldownData.get(user).lastUsedTime = System.currentTimeMillis();
    }

    /**
     * Check if the cooldown of given user is ready.
     *
     * @param user     The user who uses the cooldown
     * @param cooldown The defined cooldown duration (in second)
     * @return True if the cooldown is ready (i.e. by design, user can again
     * uses the functionality corresponding to the cooldown).
     */
    public static boolean check(UUID user, int cooldown) {
        if (cooldownData.containsKey(user)) {
            return diff(user) > cooldownData.get(user).cooldown;
        } else {
            create(user, cooldown);
            return true;
        }
    }

    /**
     * Check the remaining time for the cooldown to be ready.
     *
     * @param user     The user to be checked
     * @param cooldown The defined cooldown duration in second
     * @return The time which has to pass before the cooldown is ready.
     */
    public static long remaining(UUID user, int cooldown) {
        if (!cooldownData.containsKey(user)) {
            create(user, cooldown);
        }
        return cooldownData.get(user).cooldown - diff(user);
    }

    /**
     * Reset the cooldown of given user (i.e. forcing to let the cooldown be
     * ready)
     *
     * @param user The user to be reset cooldown
     */
    public static void reset(UUID user) {
        if (cooldownData.get(user) != null)
            cooldownData.get(user).lastUsedTime = 0;
    }

    /**
     * Reset all cooldown data.
     */
    public static void resetAll() {
        cooldownData.clear();
    }

    private static long diff(UUID user) {
        UserData userData = cooldownData.get(user);
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        return TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime);
    }

    private static void create(UUID user, int cooldown) {
        cooldownData.put(user, new UserData(cooldown));
    }

    private static class UserData {

        /**
         * The duration (in second) player has to wait for
         */
        final int cooldown;
        /**
         * The time (in millisecond) when the player last used it.
         */
        long lastUsedTime;

        UserData(int cooldown) {
            this.cooldown = cooldown;
            this.lastUsedTime = 0;
        }

    }

}
