package co.mcsky.foundores;

import org.bukkit.Location;
import org.bukkit.Material;

public interface IBlockCounter {
    /**
     * Returns the amount of blocks of same type as input that are connected to the original location.
     *
     * @param blockType The type of block that needs to count the amount.
     * @return The amount of blocks of same type that are connected.
     */
    int count(Location original, Material blockType);
}
