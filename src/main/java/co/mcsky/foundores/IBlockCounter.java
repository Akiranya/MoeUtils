package co.mcsky.foundores;

import org.bukkit.Location;

public interface IBlockCounter {
    /**
     * Returns the amount of blocks of same type as input that are connected to the original location.
     */
    int count(Location start);
}
