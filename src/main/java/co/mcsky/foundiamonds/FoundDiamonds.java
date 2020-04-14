package co.mcsky.foundiamonds;

import co.mcsky.MoeUtils;
import co.mcsky.config.FoundDiamondsConfig;
import co.mcsky.i18n.I18nBlock;
import co.mcsky.utilities.BlockFinder;
import co.mcsky.utilities.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class FoundDiamonds implements Listener {
    final private MoeUtils moe;
    final private FoundDiamondsConfig cfg;

    final private Set<Material> enabledBlocks;
    final private Map<UUID, Map<Material, Set<Location>>> locationHistory;

    final private BlockFinder finder;

    public FoundDiamonds(MoeUtils moe) {
        this.moe = moe;
        this.cfg = moe.foundDiamondsConfig;
        enabledBlocks = cfg.getBlocks();
        locationHistory = new HashMap<>();
        finder = new BlockFinder(moe);
        if (cfg.isEnable()) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("FoundDiamonds is enabled");
        }
        // Auto clear map locationHistory at given interval.
        Bukkit.getScheduler().runTaskTimer(moe, locationHistory::clear, 0, TimeUtil.toTick(cfg.getPurgeInterval()));
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!enabledBlocks.contains(block.getType())) return;
        if (!cfg.getWorlds().contains(block.getWorld().getName())) return;

        UUID playerUUID = event.getPlayer().getUniqueId();
        // New a map of location history if the player has not found any enabledBlocks yet.
        locationHistory.putIfAbsent(playerUUID, new HashMap<>());

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
         * 不过呢，把所有类型方块的位置信息存在一个巨大的 hashMap 里效率应该不低，因为查找的复杂读是 O(1)，
         * 但为了更高的查找速度，为每个玩家的每种方块创建一个专门的 hashMap 应该会更快。
         * */
        Location currentLocation = block.getLocation();
        Material currentBlock = block.getType();
        if (locationHistory.get(playerUUID).containsKey(currentBlock)) {
            Set<Location> discovered = locationHistory.get(playerUUID).get(currentBlock);
            if (!discovered.contains(currentLocation)) {
                int count = finder.count(currentLocation, currentBlock, discovered);
                broadcast(event.getPlayer(), currentBlock, count);
            }
        } else {
            locationHistory.get(playerUUID).put(currentBlock, new HashSet<>());
            int count = finder.count(currentLocation, currentBlock, locationHistory.get(playerUUID).get(currentBlock));
            broadcast(event.getPlayer(), currentBlock, count);
        }
    }

    private void broadcast(Player player, Material material, int count) {
        moe.getServer().broadcastMessage(
                String.format(
                        cfg.msg_found,
                        player.getDisplayName(),
                        count,
                        I18nBlock.getBlockDisplayName(material, player)
                ));
    }

}
