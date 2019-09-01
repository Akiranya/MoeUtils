package co.mcsky.foundores;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.bukkit.block.BlockFace.*;

/**
 * BlockFinder 的算法受到下面问题的启发：
 *
 * 问题说明：找到 graph 中所有由 1 组成的区块（chunk）。
 * <p>
 * 如果一个 1 的四个垂直/水平方向上也是 1，那么就说这些 1 是相邻的，
 * 并且此相邻性是可以传递的，也就是邻居的邻居也是邻居。
 * 以此推广，一个 1 的所有邻居就组成了一个 chunk。
 * <p>
 * 程序需要返回 graph 中所有的 chunk，每个 chunk 里需要包含其里面所有 1 的位置信息。
 * <p>
 * 例如给定下面一个图：
 * {1, 1, 1, 0, 0, 0},
 * {0, 1, 1, 1, 0, 0},
 * {0, 0, 0, 0, 1, 0},
 * {0, 0, 0, 0, 0, 0},
 * {0, 1, 1, 1, 0, 1},
 * {0, 1, 1, 1, 0, 0}
 * <p>
 * 那么应该返回如下结果：
 * Chunk 1: (0,0) (0,1) (0,2) (1,1) (1,2) (1,3)
 * Chunk 2: (2,4)
 * Chunk 3: (4,1) (4,2) (5,1) (4,3) (5,2) (5,3)
 * Chunk 4: (4,5)
 * <p>
 * P.s. 坐标的格式是 (rows, cols)
 */
public class BFSBlockFinder implements BlockFinder {

    private final MoeUtils moe;
    // 这是搜索邻居的标准，可以根据要求进行修改
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
    private int maxIterations; // 最大搜索

    public BFSBlockFinder(MoeUtils moe, int maxIterations) {
        this.moe = moe;
        this.maxIterations = maxIterations;
    }

    @Override
    public int count(Location start, Material targetBlockType, Set<Location> discovered) {
        return BFS(start, targetBlockType, discovered);
    }

    /**
     * @param start 起始点
     * @param targetBlockType 搜索的方块类型
     * @param discovered 已探索过的坐标
     * @return 起始点方块的所有合法邻居数量（包括本身）
     */
    private int BFS(Location start, Material targetBlockType, Set<Location> discovered) {
        if (discovered == null) {
            moe.getLogger().warning("BlockFinder is not initialized! Search abort.");
            return 0;
        }

        Queue<Location> queue = new LinkedList<>();

        queue.add(start); // 添加 start_vertex 到 queue
        discovered.add(start); // 标记 start_vertex 为已探索

        int count = 0; // 数遍历了多少个 vertex

        while (!queue.isEmpty()) {
            Location v = queue.remove();

            if (++count >= maxIterations) return count; // 达到最大迭代数时，直接返回当前的方块数，不再进一步搜索

            // 遍历 vertex 的所有邻居（这里有 neighbor.length 个邻居）
            // 邻居的标准可以根据情况随时修改，所以考虑加个 config
            for (BlockFace neighbor : neighbors) {
                Location nei = v.getBlock().getRelative(neighbor).getLocation(); // get 邻居的坐标
                if (!isDiscovered(nei, discovered) && isLegalBlock(nei, targetBlockType)) {
                    // 这里 IF 需要满足：
                    // 2、邻居 v 没有被探索
                    // 3、邻居 v 是目标方块
                    queue.add(nei); // 把邻居加进 queue
                    discovered.add(nei); // 把邻居标记为已探索
                }
            }
        }

        return count;
    }

    /**
     * @return 是否已经探索过
     */
    private boolean isDiscovered(Location target, Set<Location> discovered) {
        return discovered.contains(target);
    }

    /**
     * @return 是否为合法方块（i.e. 该方块是否设定为应该进行全服通告）
     */
    private boolean isLegalBlock(Location target, Material targetBlockType) {
        return target.getBlock().getType() == targetBlockType;
    }

}
