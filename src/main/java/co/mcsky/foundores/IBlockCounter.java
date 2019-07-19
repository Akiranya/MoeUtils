package co.mcsky.foundores;

import org.bukkit.Location;
import org.bukkit.Material;

public interface IBlockCounter {
    /**
     * Implementation should return the amount of blocks of same type as input that are connected to the original location.
     *
     * @param blockType Original block.
     * @return The amount of blocks of same type as input that are connected to the original.
     */
    int count(Location original, Material blockType);
}
