package co.mcsky.foundores;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Set;
import java.util.Stack;

public class DFSBlockFinder extends ABlockFinder implements IBlockFinder {

    DFSBlockFinder(MoeUtils moe) {
        super(moe);
    }

    @Override
    public int count(Location start, Material targetBlockType, Set<Location> discovered) {
        return DFS(start, targetBlockType, discovered);
    }

    /**
     * @param start           起始点
     * @param targetBlockType 搜索的方块类型
     * @param discovered      已探索过的坐标
     * @return 起始点方块的所有合法邻居数量（包括本身）
     */
    private int DFS(Location start, Material targetBlockType, Set<Location> discovered) {
        if (discovered == null) {
            moe.getLogger().warning("BlockFinder is not initialized! Search abort.");
            return 0;
        }

        Stack<Location> stack = new Stack<>();
        stack.push(start); // 添加 start_vertex 到 stack

        int count = 0; // 数遍历了多少个 vertex

        while (!stack.isEmpty()) {
            Location v = stack.pop();

            if (!isDiscovered(v, discovered)) {
                // 执行到这一步说明 v 一定是合法方块
                // 因此不需要再判断是否合法
                discovered.add(v); // 把 v 标记为已探索

                // 标记好已探索后，在这里进行相关操作
                if (++count >= searchBound) return count; // 达到最大迭代数时，直接返回当前的方块数，不再进一步搜索

                // 遍历 v 的所有邻居（一共有 neighbor.length 个邻居）
                // 邻居的标准可以根据情况随时修改，所以考虑加个 config 吧
                for (BlockFace neighbor : neighbors) {
                    Location nei = v.getBlock().getRelative(neighbor).getLocation();
                    if (isLegalBlock(nei, targetBlockType)) {
                        stack.push(nei); // 把邻居（的坐标）加进 stack
                    }
                }
            }
        }

        return count;
    }

}
