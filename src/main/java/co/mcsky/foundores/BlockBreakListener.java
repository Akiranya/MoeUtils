package co.mcsky.foundores;

import co.mcsky.MoeConfig;
import co.mcsky.MoeUtils;
import co.mcsky.utils.MoeLib;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
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
     * <p>The reason why I use Deque will be explained someday later :)</p>
     */
    final private Deque<Region> foundLocations;
    private RegionHandler regionHandler;

    public BlockBreakListener(MoeUtils moe) {
        this.moe = moe;
        blockTypeMap = moe.config.foundores_block_types;
        blockTypes = blockTypeMap.keySet();
        foundLocations = new LinkedList<>();
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            regionHandler = new RegionHandler();
            // Schedule a task which remove the last element of Deque at given interval
            Bukkit.getScheduler().runTaskTimer(moe, () -> {
                if (foundLocations.isEmpty()) return;
                foundLocations.removeLast();
            }, 0, MoeLib.toTick(moe.config.foundores_pop_interval));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        // Is block ore?
        if (!blockTypes.contains(block.getType())) {
            return;
        }
        // Is block in region previously created?
        // If it is, simply returns, no need for further checking.
        BlockVector3 vector = BlockVector3.at(block.getX(), block.getY(), block.getZ());
        for (Region r : foundLocations) {
            if (r.contains(vector)) return;
        }
        // Is player in survival mode?
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        // Is block in enabled worlds?
        if (!moe.config.foundores_worlds.contains(block.getWorld().getName())) {
            return;
        }
        // If the block is not in any region previously created,
        // adds the region which the block are from to the head of queue.
        World world = BukkitAdapter.adapt(block.getWorld());
        CuboidRegion region = regionHandler.createCuboidRegion(world, vector, moe.config.foundores_check_radius);
        foundLocations.addFirst(region);
        // And broadcast it!
        broadcast(event, block, region);
    }

    /**
     * Broadcasts that a player has found an ore!
     *
     * @param event The event.
     * @param block The (ore) block related to this event.
     */
    private void broadcast(BlockBreakEvent event, Block block, Region region) {
        String playerName = event.getPlayer().getName();
        String blockType = block.getType().name();
        String color = blockTypeMap.get(Material.matchMaterial(blockType));
        moe.getServer().broadcastMessage(
                MoeConfig.color(String.format(
                        moe.config.foundores_message_found,
                        playerName, regionHandler.countBlock((CuboidRegion) region, block.getType()), color, blockType)
                )
        );
    }
}
