package co.mcsky.foundores;

import co.mcsky.MoeConfig;
import co.mcsky.MoeUtils;
import co.mcsky.utils.MoeLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class BlockBreakListener implements Listener {
    final private MoeUtils moe;
    // K -> block_type
    // V -> color_code
    final private Map<Material, String> blockTypeMap;
    final private Queue<PlayerBlockPair> discoveredLocation; // Stores locations of blocks (ores) where players have explored.

    public BlockBreakListener(MoeUtils moe) {
        this.moe = moe;
        blockTypeMap = moe.config.foundores_block_types;
        discoveredLocation = new LinkedList<>();
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            // Schedule a task which removes the last element of Deque at given interval
            Bukkit.getScheduler().runTaskTimer(moe, discoveredLocation::poll, 0, MoeLib.toTick(moe.config.foundores_pop_interval));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (discoveredLocation.isEmpty()) {
            return;
        }

        Block block = event.getBlock();
        // Is block ore?
        if (!blockTypeMap.keySet().contains(block.getType())) {
            return;
        }

        Material blockType = block.getType();
        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerBlockPair peek = discoveredLocation.peek();
        if (peek.player.equals(playerUUID) && peek.blockType == blockType) {
            return;
        }

        // Is block in enabled worlds?
        if (!moe.config.foundores_worlds.contains(block.getWorld().getName())) {
            return;
        }

        // Is player in survival mode?
//        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
//            return;
//        }

        // If the block is not in any region previously created,
        // adds the region which the block are from to the head of queue.
        discoveredLocation.add(new PlayerBlockPair(playerUUID, blockType));
        broadcast(event, block); // Then broadcast it!
    }

    /**
     * Broadcasts that a player has found an ore!
     *
     * @param event The event.
     * @param block The (ore) block related to this event.
     */
    private void broadcast(BlockBreakEvent event, Block block) {
        String playerName = event.getPlayer().getName();
        String blockType = block.getType().name();
        String color = blockTypeMap.get(Material.matchMaterial(blockType));
        String msg = MoeConfig.color(String.format(moe.config.foundores_message_found, playerName, color, blockType));
        moe.getServer().broadcastMessage(msg);
    }

    private class PlayerBlockPair {
        UUID player;
        Material blockType;

        PlayerBlockPair(UUID player, Material blockType) {
            this.player = player;
            this.blockType = blockType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PlayerBlockPair)) return false;
            return Objects.equals(player, o);
        }
    }

}
