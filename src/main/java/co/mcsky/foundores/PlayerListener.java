package co.mcsky.foundores;

import co.mcsky.MoeConfig;
import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

import static co.mcsky.utils.MoeLib.toTick;

public class PlayerListener implements Listener {
    final private MoeUtils moe;
    final private String COOLDOWNKEY = "foundores";

    // K -> block_type
    // V -> color_code
    final private Map<Material, String> blockTypeMap;
    final private Map<UUID, Stack<PlayerBlockPair>> playerBlockLog;
    private final int clearTask;
    private final int reduceTask;

    public PlayerListener(MoeUtils moe) {
        this.moe = moe;
        blockTypeMap = moe.config.foundores_block_types;
        playerBlockLog = new HashMap<>();
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }

        // Auto clear map playerBlockLog at given interval
        clearTask = Bukkit.getScheduler().runTaskTimerAsynchronously(moe, () -> {
            playerBlockLog.clear();
            fillPlayerBlockLog();
        }, 0, toTick(moe.config.foundores_purge_interval)).getTaskId();

        reduceTask = Bukkit.getScheduler().runTaskTimerAsynchronously(moe, () -> {
            if (playerBlockLog.isEmpty()) return;
            playerBlockLog.forEach((UUID player, Stack<PlayerBlockPair> stack) -> {
                // Reduces size of stacks of all players at given interval.
                // This just ensures that if a player still finds same type of ores as they did after a while,
                // we should announce them.
                if (!stack.isEmpty()) stack.pop();
            });
        }, 0, toTick(moe.config.foundores_pop_interval)).getTaskId();

        fillPlayerBlockLog();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // OreStack must be created as soon as players join server
        UUID playerUUID = event.getPlayer().getUniqueId();
        playerBlockLog.putIfAbsent(playerUUID, new Stack<>());
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        // Is block ore?
        if (!blockTypeMap.keySet().contains(block.getType())) {
            return;
        }

        Material blockType = block.getType();
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (!playerBlockLog.get(playerUUID).isEmpty()) {
            PlayerBlockPair peek = playerBlockLog.get(playerUUID).peek();
            if (peek.player.equals(playerUUID) && peek.blockType == blockType) {
                return;
            }
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
        playerBlockLog.get(playerUUID).push(new PlayerBlockPair(playerUUID, blockType));
        broadcast(event, block); // Then broadcast it!
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(clearTask);
        Bukkit.getScheduler().cancelTask(reduceTask);
    }

    /**
     * Broadcasts that a player has found an ore!
     *
     * @param event The event.
     * @param block The (ore) block related to this event.
     */
    private void broadcast(BlockBreakEvent event, Block block) {
        String playerName = event.getPlayer().getDisplayName();
        Material blockType = block.getType();
        String color = blockTypeMap.get(Material.matchMaterial(blockType.name()));
        String trans = moe.config.foundores_message_block_translation.get(blockType);
        String msg = MoeConfig.color(String.format(moe.config.foundores_message_found, playerName, color, trans));
        moe.getServer().broadcastMessage(msg);
    }

    private void fillPlayerBlockLog() {
        // In case of reading empty map
        Bukkit.getOnlinePlayers().forEach(p -> playerBlockLog.put(p.getUniqueId(), new Stack<>()));
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