package co.mcsky.foundores;

import co.mcsky.MoeConfig;
import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

import static co.mcsky.utils.MoeLib.toTick;

public class PlayerListener implements Listener {
    final private MoeUtils moe;
    final private String COOLDOWN_KEY = "foundores";

    // K -> block_type
    // V -> color_code
    final private Map<Material, String> legalBlockType;
    final private Map<UUID, Map<Material, Set<Location>>> playerLog;

    final private BlockFinder finder;

    public PlayerListener(MoeUtils moe) {
        this.moe = moe;

        legalBlockType = moe.config.foundores_block_types; // 哪些方块需要通报
        playerLog = new HashMap<>();
        finder = new BlockFinder(moe);

        // 根据配置文件判断是否要注册 Listener
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }

        // Auto clear map playerLog at given interval
        Bukkit.getScheduler().runTaskTimerAsynchronously(moe, () -> {
            playerLog.clear();
            reload();
        }, 0, toTick(moe.config.foundores_purge_interval));

        reload();
    }

//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        UUID playerUUID = event.getPlayer().getUniqueId();
//        playerLog.putIfAbsent(playerUUID, new HashMap<>(new HashMap<>()));
//    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        // 如果挖到的矿不在配置文件里，直接 return
        if (!legalBlockType.keySet().contains(block.getType())) return;

        // 如果当前世界未启用通告，直接 return
        if (!moe.config.foundores_worlds.contains(block.getWorld().getName())) return;

        // 如果玩家不是生存模式，直接 return
//        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        // 这些本地变量下面都会用到
        Material blockType = block.getType();
        UUID playerUUID = event.getPlayer().getUniqueId();

        Location justFound = block.getLocation(); // 玩家当前挖掉的方块坐标

        playerLog.putIfAbsent(playerUUID, new HashMap<>());
        Map<Material, Set<Location>> typeLog = playerLog.get(playerUUID);
        // 继续 ...

        if (typeLog.containsKey(blockType)) { // 如果玩家发现过这种类型的方块
            if (!typeLog.get(blockType).contains(justFound)) { // 如果玩家还没发现过这个位置的方块，则进行通报
                // 开始搜索
                int count = finder.BFS(justFound, blockType, typeLog.get(blockType));
                // 通报
                broadcast(event.getPlayer().getDisplayName(), blockType, count);
            }
            // 如果玩家已经发现过这个位置的方块，直接返回
        } else { // 如果玩家还没发现过这种方块，则进行通报
            Set<Location> discovered = new HashSet<>();
            typeLog.put(blockType, discovered);

            // 开始搜索
            int count = finder.BFS(justFound, blockType, typeLog.get(blockType));

            broadcast(event.getPlayer().getDisplayName(), blockType, count);
        }
    }

    /**
     * 全服通告玩家挖到矿了!
     */
    private void broadcast(String player, Material blockType, int count) {
        String serverMsg = String.format(
                moe.config.foundores_message_found,
                player,
                count,
                legalBlockType.get(Material.matchMaterial(blockType.name())));
        String colorServerMsg = MoeConfig.color(serverMsg);
        moe.getServer().broadcastMessage(colorServerMsg);
    }

    private void reload() {
        // In case of reading empty map
        Bukkit.getOnlinePlayers().forEach(p -> playerLog.put(p.getUniqueId(), null));
    }

}
