package co.mcsky.utils;

import org.bukkit.Location;
import org.bukkit.WorldBorder;

import java.util.concurrent.TimeUnit;

public class MoeLib {

    /**
     * Check if the location is out of border of the world of given location.
     *
     * @param loc The location to be checked.
     * @return True if the location is out of border, otherwise false.
     */
    public static boolean isOutsideOfBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    /**
     * Converts seconds to minecraft tick.
     *
     * @param seconds Seconds
     * @return Minecraft ticks
     */
    public static int toTick(int seconds) {
        return seconds * 20;
    }

    /**
     * @param now          In millisecond.
     * @param lastUsedTime In millisecond.
     * @param cooldown     In second. The duration player has to wait for.
     * @return The data about cooldown.
     */
    public static Cooldown cooldown(long now, long lastUsedTime, int cooldown) {
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (diff <= cooldown) { // Note that cooldown is in second
            int remaining = (int) (cooldown - diff);
            return new Cooldown(false, remaining);
        } else {
            return new Cooldown(true, 0);
        }
    }
}
