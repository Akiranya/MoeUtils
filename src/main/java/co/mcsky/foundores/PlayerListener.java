package co.mcsky.foundores;

import co.mcsky.MoeUtils;
import co.mcsky.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class PlayerListener implements Listener {
    final private MoeUtils moe;

    // K -> block type
    // V -> translation
    final private Map<Material, String> legalBlockType;
    final private Map<UUID, Map<Material, Set<Location>>> playerLog;

    final private IBlockFinder finder;

    public PlayerListener(MoeUtils moe) {
        this.moe = moe;

        legalBlockType = moe.setting.found_diamond.enabled_block_type; // 哪些方块需要通报
        playerLog = new HashMap<>();
        finder = new BFSBlockFinder(moe); // 初始化搜索类

        // 根据配置文件判断是否要注册 Listener
        if (moe.setting.found_diamond.enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }

        // Auto clear map playerLog at given interval
        Bukkit.getScheduler().runTaskTimer(moe, playerLog::clear, 0, TimeUtil.toTick(moe.setting.found_diamond.purge_interval));
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        // 如果挖到的矿不在配置文件里，直接 return
        if (!legalBlockType.keySet().contains(block.getType())) return;

        // 如果当前世界未启用通告，直接 return
        if (!moe.setting.found_diamond.enabled_world.contains(block.getWorld().getName())) return;

        // 如果玩家不是生存模式，直接 return
//        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        Location foundLocation = block.getLocation(); // 玩家当前挖掉的方块坐标
        Material foundType = block.getType(); // 玩家当前挖掉的方块类型

        UUID playerUUID = event.getPlayer().getUniqueId();
        playerLog.putIfAbsent(playerUUID, new HashMap<>()); // 如果该玩家从没挖到过矿，给玩家创建一个 map
        Map<Material, Set<Location>> typeLog = playerLog.get(playerUUID);

        /* 由于这是一块可能会高频率执行的代码，
         * 特别是搜索方块的数量涉及到深搜/广搜，
         * 因此需要尽可能的提高代码的运行效率，以减轻 CPU 负载，
         * 故设计了以下流程。
         *
         * 首先分两种情况：
         * 1) 玩家发现过这种*类型*的方块
         * 2) 玩家未发现过这种*类型*的方块
         *
         * 如果是 1) 那么从这种*类型*的方块里搜索是否探索过这个*位置*
         * 如果是 2) 那么就从零开始考虑，也就是需要为玩家和方块创建相应的 map
         *
         * 不过呢，把所有类型方块的位置信息存在一个巨大的 hashMap 里效率也许也不错，因为查找的复杂读是 O(1)，
         * 但为了更高的查找速度，为每个玩家的每种方块创建一个专门的 hashMap 应该会更快。
         * */
        if (typeLog.containsKey(foundType)) {
            Set<Location> discovered = typeLog.get(foundType);
            if (!discovered.contains(foundLocation)) {
                int count = finder.count(foundLocation, foundType, discovered);
                broadcast(event.getPlayer().getDisplayName(), foundType, count);
            }
        } else {
            typeLog.put(foundType, new HashSet<>());
            int count = finder.count(foundLocation, foundType, typeLog.get(foundType));
            broadcast(event.getPlayer().getDisplayName(), foundType, count);
        }
    }

    private void broadcast(String player, Material blockType, int count) {
        String raw = String.format(moe.setting.found_diamond.msg_found,
                player,
                count,
                legalBlockType.get(Material.matchMaterial(blockType.name())));
        String broadcast = ChatColor.translateAlternateColorCodes('&', raw);
        moe.getServer().broadcastMessage(broadcast);
    }

}
