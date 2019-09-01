package co.mcsky.foundores;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

public interface BlockFinder {
    /**
     * 返回指定方块的所有邻居个数。邻居的标准可以自行设定。
     */
    int count(Location start, Material targetBlockType);

    /**
     * 该方法技术上来看必须要实现，但实际上需要根据最终的实现来决定是否需要切实实现。
     * 如果没有实现的必要，可以实现一个空方法。
     *
     * @param discovered 已探索过的坐标信息
     */
    void setDiscovered(Set<Location> discovered);
}
