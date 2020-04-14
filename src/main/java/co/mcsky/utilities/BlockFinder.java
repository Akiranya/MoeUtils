package co.mcsky.utilities;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.bukkit.block.BlockFace.*;

public class BlockFinder {

    /**
     * The maximum iteration for searching.
     */
    private final int searchBound;

    /**
     * 这是搜索邻居的标准，可以根据要求进行修改。
     */
    private final BlockFace[] neighbors = {
            NORTH,
            EAST,
            SOUTH,
            WEST,
            UP,
            DOWN,
            NORTH_EAST,
            NORTH_WEST,
            SOUTH_EAST,
            SOUTH_WEST,
            WEST_NORTH_WEST,
            NORTH_NORTH_WEST,
            NORTH_NORTH_EAST,
            EAST_NORTH_EAST,
            EAST_SOUTH_EAST,
            SOUTH_SOUTH_EAST,
            SOUTH_SOUTH_WEST,
            WEST_SOUTH_WEST
    };

    public BlockFinder(MoeUtils moe) {
        this.searchBound = moe.foundDiamondsConfig.getMaxIterations();
    }

    /**
     * @return 是否已经探索过
     */
    private boolean isDiscovered(Location target, Set<Location> discovered) {
        return !discovered.contains(target);
    }

    /**
     * @return 是否为合法方块（i.e. 该方块是否设定为应该进行全服通告）
     */
    private boolean isLegalBlock(Location target, Material targetBlockType) {
        return target.getBlock().getType() == targetBlockType;
    }

    /**
     * @param start      Start location.
     * @param target     The type of block you want to search for.
     * @param discovered A set containing locations where blocks are already marked as discovered on which the finder
     *                   will not count.
     *
     * @return The number of blocks of same type as {@code target} which are the neighbors of the block at location
     * {@code start}.
     */
    public int count(Location start, Material target, Set<Location> discovered) {
        if (discovered == null) {
            return 0;
        }
        Queue<Location> queue = new LinkedList<>();
        queue.add(start); // 添加 start_vertex 到 queue
        discovered.add(start); // 标记 start_vertex 为已探索
        int count = 0; // 数遍历了多少个 vertex
        while (!queue.isEmpty()) {
            Location v = queue.remove();
            // 达到最大迭代数时，直接返回当前的方块数，不再进一步搜索
            if (++count >= searchBound) return count;
            // 遍历 vertex 的所有邻居（这里有 neighbor.length 个邻居）
            // 邻居的标准可以根据情况随时修改，所以考虑加个 setting
            for (BlockFace neighbor : neighbors) {
                Location nei = v.getBlock().getRelative(neighbor).getLocation(); // get 邻居的坐标
                if (isDiscovered(nei, discovered) && isLegalBlock(nei, target)) {
                    // 这里 IF 需要满足：
                    // 1、邻居 v 没有被探索
                    // 2、邻居 v 是目标方块
                    queue.add(nei); // 把邻居加进 queue
                    discovered.add(nei); // 把邻居标记为已探索
                }
            }
        }
        return count;
    }

}
