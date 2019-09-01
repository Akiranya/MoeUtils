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

    // K -> block type
    // V -> translation
    final private Map<Material, String> legalBlockType;
    final private Map<UUID, Map<Material, Set<Location>>> playerLog;

    final private IBlockFinder finder;

    public PlayerListener(MoeUtils moe) {
        this.moe = moe;

        legalBlockType = moe.config.foundores_block_types; // 哪些方块需要通报
        playerLog = new HashMap<>();
        finder = new DFSIBlockFinder(moe); // 初始化搜索类

        // 根据配置文件判断是否要注册 Listener
        if (moe.config.foundores_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }

        // Auto clear map playerLog at given interval
        Bukkit.getScheduler().runTaskTimer(moe, () -> {
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
        UUID playerUUID = event.getPlayer().getUniqueId();
        Location foundLocation = block.getLocation(); // 玩家当前挖掉的方块坐标
        Material foundType = block.getType(); // 玩家当前挖掉的方块类型
        playerLog.putIfAbsent(playerUUID, new HashMap<>()); // 如果该玩家从没挖到过矿，给玩家创建一个 map
        Map<Material, Set<Location>> typeLog = playerLog.get(playerUUID); // 方块 + 其对应的已探索坐标集合
        if (typeLog.containsKey(foundType)) {
            // 如果玩家发现过这种类型的方块

            Set<Location> discovered = typeLog.get(foundType);
            if (!discovered.contains(foundLocation)) {
                // 如果玩家还没发现过这个位置的方块，则进行通报

                int count = finder.count(foundLocation, foundType, discovered); // 开始搜索
                broadcast(event.getPlayer().getDisplayName(), foundType, count); // 然后通报
            }
            // 如果玩家已经发现过这个位置的方块，不做计算

        } else {
            // 如果玩家还没发现过这种类型的方块，则进行通报

            // 因为玩家连这种类型的方块都没发现过，
            // 所以需要*创建*一个 discovered set 给当前方块类型
            typeLog.put(foundType, new HashSet<>()); // 不要忘记放到 typeLog map 里供之后的检索用

            int count = finder.count(foundLocation, foundType, typeLog.get(foundType)); // 开始搜索
            broadcast(event.getPlayer().getDisplayName(), foundType, count); // 然后通报
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
