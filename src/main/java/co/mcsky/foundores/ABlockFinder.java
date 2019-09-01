package co.mcsky.foundores;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Set;

import static org.bukkit.block.BlockFace.*;

abstract class ABlockFinder {

    final MoeUtils moe;
    final int searchBound;

    // 这是搜索邻居的标准，可以根据要求进行修改
    final BlockFace[] neighbors = {
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

    ABlockFinder(MoeUtils moe) {
        this.moe = moe;
        this.searchBound = moe.config.foundores_max_iterations;
    }

    /**
     * @return 是否已经探索过
     */
    boolean isDiscovered(Location target, Set<Location> discovered) {
        return discovered.contains(target);
    }

    /**
     * @return 是否为合法方块（i.e. 该方块是否设定为应该进行全服通告）
     */
    boolean isLegalBlock(Location target, Material targetBlockType) {
        return target.getBlock().getType() == targetBlockType;
    }

}

