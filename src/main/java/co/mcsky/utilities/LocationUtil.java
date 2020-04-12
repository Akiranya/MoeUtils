package co.mcsky.utilities;

import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class LocationUtil {

    /**
     * Check if the location is out of border of the world of given location.
     *
     * @param loc The location to be checked.
     * @return True if the location is out of border, otherwise false.
     */
    public static boolean outBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

}
