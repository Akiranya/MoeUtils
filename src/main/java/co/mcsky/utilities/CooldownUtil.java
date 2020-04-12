package co.mcsky.utilities;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用来计算冷却时间的工具类。
 *
 * @author Nailm
 */
public class CooldownUtil<K> {

    private final Map<K, UserData> cooldownMap;

    protected CooldownUtil() {
        cooldownMap = new HashMap<>();
    }

    protected void use(K user) throws NullPointerException {
        Validate.notNull(cooldownMap.get(user), "specific key doesn't exist.");
        cooldownMap.get(user).lastUsedTime = System.currentTimeMillis();
    }

    protected boolean check(K user, int cooldown) {
        if (cooldownMap.containsKey(user)) {
            return diff(user) > cooldownMap.get(user).cooldown;
        } else {
            create(user, cooldown);
            return true;
        }
    }

    protected int remaining(K user, int cooldown) {
        if (!cooldownMap.containsKey(user)) {
            create(user, cooldown);
        }
        return (cooldownMap.get(user).cooldown - diff(user));
    }

    protected void reset(K user) {
        if (cooldownMap.get(user) != null)
            cooldownMap.get(user).lastUsedTime = 0;
    }

    private int diff(K user) {
        UserData userData = cooldownMap.get(user);
        long now = System.currentTimeMillis();
        long lastUsedTime = userData.lastUsedTime;
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        return (int) diff;
    }

    // 如果要给一个用户创建 cooldown 的数据，直接使用 check() 就好啦
    private void create(K user, int cooldown) {
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
