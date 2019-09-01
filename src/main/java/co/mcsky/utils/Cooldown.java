package co.mcsky.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Carries the data about cooldown.
 */
public class Cooldown {

    private final Map<String, UserData> cooldownMap;

    protected Cooldown() {
        cooldownMap = new HashMap<>();
    }

    protected void use(String user) throws NullPointerException {
        if (!cooldownMap.containsKey(user)) {
            throw new NullPointerException("specific key doesn't exist.");
        }
        cooldownMap.get(user).lastUsedTime = System.currentTimeMillis();
    }

    protected boolean check(String user, int cooldown) {
        if (cooldownMap.containsKey(user)) {
            return diff(user) > cooldownMap.get(user).cooldown;
        } else {
            create(user, cooldown);
            return true;
        }
    }

    protected int remaining(String user, int cooldown) {
        if (!cooldownMap.containsKey(user)) {
            create(user, cooldown);
        }
        return (cooldownMap.get(user).cooldown - diff(user));
    }

    protected void reset(String user) {
        cooldownMap.get(user).lastUsedTime = 0;
    }

    private int diff(String user) {
        UserData userData = cooldownMap.get(user);
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        return (int) diff;
    }

    // 如果要给一个用户创建 cooldown 的数据，直接使用 check() 就好啦
    private void create(String user, int cooldown) {
        cooldownMap.put(user, new UserData(cooldown, 0));
    }

    private class UserData {
        int cooldown; // In second. The duration player has to wait for.
        long lastUsedTime; // In millisecond. The time when the player last used it.

        UserData(int cooldown, long lastUsedTime) {
            this.cooldown = cooldown;
            this.lastUsedTime = lastUsedTime;
        }

    }
}
