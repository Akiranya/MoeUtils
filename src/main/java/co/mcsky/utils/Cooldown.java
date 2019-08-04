package co.mcsky.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Carries the data about cooldown.
 */
public class Cooldown {

    private static Cooldown cooldownInstance = null;
    private static Map<String, UserData> cooldownMap;

    private Cooldown() {
        cooldownMap = new HashMap<>();
    }

    public static Cooldown getInstance() {
        if (cooldownInstance != null) {
            return cooldownInstance;
        }
        return cooldownInstance = new Cooldown();
    }

    public void use(String user) throws NullPointerException {
        if (!cooldownMap.containsKey(user)) {
            throw new NullPointerException("UserData not created yet.");
        }

        UserData userData = cooldownMap.get(user);
        userData.lastUsedTime = System.currentTimeMillis();
        cooldownMap.put(user, userData);
    }

    public boolean check(String user, int cooldown) throws NullPointerException {
        if (!cooldownMap.containsKey(user)) {
            create(user, cooldown);
            return true;
        }
        UserData userData = cooldownMap.get(user);
        return diff(user) > userData.cooldown;
    }

    public int remaining(String user, int cooldown) throws NullPointerException {
        if (!cooldownMap.containsKey(user)) {
            create(user, cooldown);
        }

        UserData userData = cooldownMap.get(user);
        return (userData.cooldown - diff(user));
    }

    public void reset(String user) {
        UserData userData = cooldownMap.get(user);
        userData.lastUsedTime = 0;
        cooldownMap.put(user, userData);
    }

    /**
     * Returns the "progress" of given user cooldown.
     *
     * For example, if the cooldown is 600,
     * then when progress (diff) is 600, that means the cooldown IS ready.
     *
     * @param user User
     * @return Progress.
     */
    private int diff(String user) {
        UserData userData = cooldownMap.get(user);
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        return (int) diff;
    }

    private void create(String user, int cooldown) {
        UserData userData = new UserData();
        userData.cooldown = cooldown;
//        userData.lastUsedTime = 0; // implicitly to be zero
        cooldownMap.put(user, userData);
    }

    private class UserData {

        /**
         * In second. The duration player has to wait for.
         */
        int cooldown;
        /**
         * In millisecond.
         */
        long lastUsedTime;
    }

}
