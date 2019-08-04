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
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        // Note that cooldown is in second
        return diff > userData.cooldown;
    }

    public int remaining(String user, int cooldown) throws NullPointerException {
        if (!cooldownMap.containsKey(user)) {
            create(user, cooldown);
        }

        UserData userData = cooldownMap.get(user);
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        return (int) (userData.cooldown - diff);
    }

    public void reset(String user) {
        UserData userData = cooldownMap.get(user);
        userData.lastUsedTime = 0;
        cooldownMap.put(user, userData);
    }

    private void create(String user, int cooldown) {
        UserData userData = new UserData();
        userData.cooldown = cooldown;
//        userData.lastUsedTime = 0; // implicitly to be zero
        cooldownMap.put(user, userData);
    }

    public static Cooldown getInstance() {
        if (cooldownInstance != null) {
            return cooldownInstance;
        } else {
            return cooldownInstance = new Cooldown();
        }
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
