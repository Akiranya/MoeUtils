package co.mcsky.foundores;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.Material;

public class RegionHandler {

    /**
     * Creates a cuboid region with given radius and center location.
     *
     * @param world  World in which the region is.
     * @param vector The center of the cuboid region. BlockVector3 object can be retrieved by invoking BlockVector3.at().
     * @param radius Radius of the cuboid region.
     * @return A cuboid region with given radius and center location.
     */
    public CuboidRegion createCuboidRegion(World world, BlockVector3 vector, int radius) {
        int x1 = vector.getBlockX() - radius;
        int y1 = vector.getBlockY() - radius;
        int z1 = vector.getBlockZ() - radius;
        int x2 = vector.getBlockX() + radius;
        int y2 = vector.getBlockY() + radius;
        int z2 = vector.getBlockZ() + radius;
        return new CuboidRegion(world, BlockVector3.at(x1, y1, z1), BlockVector3.at(x2, y2, z2));
    }

    /**
     * Count the number of blocks of given type in specified cuboid region.
     *
     * @param region The region to be checked.
     * @return The number of blocks of given type in specified region.
     */
    public int countBlock(CuboidRegion region, Material type) {
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();
        int minX = pos1.getX();
        int minY = pos1.getY();
        int minZ = pos1.getZ();
        int maxX = pos2.getX();
        int maxY = pos2.getY();
        int maxZ = pos2.getZ();
        int count = 0;

        // The best way to get a Bukkit world from WorldEdit world I have found now.
        BukkitWorld bukkitWorld = BukkitAdapter.asBukkitWorld(region.getWorld());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(bukkitWorld.getWorld(), x, y, z);
                    if (loc.getBlock().getType() == type) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
