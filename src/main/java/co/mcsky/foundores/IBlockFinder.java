package co.mcsky.foundores;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

public interface IBlockFinder {
    /**
     * @param start           起始坐标
     * @param targetBlockType 要搜索的方块的类型
     * @param discovered      该类型方块的已探索过的坐标集合
     * @return 返回指定方块的所有邻居个数。邻居的标准可以根据实现而定。
     */
    int count(Location start, Material targetBlockType, Set<Location> discovered);
}
