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
}
