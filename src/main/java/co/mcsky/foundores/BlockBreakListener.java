package co.mcsky.foundores;

import co.mcsky.MoeConfig;
import co.mcsky.MoeUtils;
import co.mcsky.utils.MoeLib;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class BlockBreakListener implements Listener {
    final private MoeUtils moe;
    /**
     * K = block_type<br>
     * V = color_code
     */
    final private Map<Material, String> blockTypeMap;
    /**
     * <p>Block types which need to broadcast when players break them.</p>
     * <p>Using HashSet for constant searching time.</p>
     */
    final private Set<Material> blockTypes;
    /**
     * <p>Stores locations of blocks (ores) where players have explored.</p>
     * <p>Using HashSet for constant searching time.</p>
     */
    final private Deque<CuboidRegion> foundLocations;

    public BlockBreakListener(MoeUtils moe) {
        this.moe = moe;
        blockTypeMap = moe.config.foundores_block_types;
        blockTypes = blockTypeMap.keySet();
        foundLocations = new LinkedList<>();
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            Bukkit.getScheduler().runTaskTimer(moe, () -> {
                if (foundLocations.isEmpty()) return;
                foundLocations.removeLast();
            }, 0, MoeLib.toTick(10));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Block b = e.getBlock();
        // Is block ore?
        if (!blockTypes.contains(b.getType())) {
            return;
        }
        // Is player in survival mode?
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        // Is block in region?
        BlockVector3 vector = BlockVector3.at(b.getX(), b.getY(), b.getZ());
        for (Region r : foundLocations) {
            if (r.contains(vector)) return;
        }
        // If the block is not in any region previously created,
        // adds it to the head of queue.
        CuboidRegion region = createCuboidRegion(vector, 1);
        foundLocations.addFirst(region);
        broadcast(e, b);
    }

    /**
     * Broadcasts that a player has found an ore!
     *
     * @param e The event.
     * @param b The block related to this event.
     */
    private void broadcast(BlockBreakEvent e, Block b) {
        String playerName = e.getPlayer().getName();
        String blockType = b.getType().name();
        String color = blockTypeMap.get(Material.matchMaterial(blockType));
        moe.getServer().broadcastMessage(
                MoeConfig.color(String.format(
                        moe.config.foundores_message_found,
                        playerName, 999, color, blockType)
                )
        );
    }

    /**
     * Creates a cuboid region with given radius and center location.
     *
     * @param v The center of the cuboid region. BlockVector3 object can be retrieved by invoking BlockVector3.at().
     * @param radius Radius of the cuboid region.
     * @return A cuboid region with given radius and center location.
     */
    private CuboidRegion createCuboidRegion(BlockVector3 v, int radius) {
        int x1 = v.getBlockX() - radius;
        int y1 = v.getBlockY() - radius;
        int z1 = v.getBlockZ() - radius;
        int x2 = v.getBlockX() - radius;
        int y2 = v.getBlockY() - radius;
        int z2 = v.getBlockZ() - radius;
        return new CuboidRegion(BlockVector3.at(x1, y1, z1), BlockVector3.at(x2, y2, z2));
    }
}
