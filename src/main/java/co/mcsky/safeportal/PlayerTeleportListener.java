package co.mcsky.safeportal;

import co.mcsky.MoeUtils;
import co.mcsky.utilities.LocationUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerTeleportListener implements Listener {

    private final MoeUtils moe;

    public PlayerTeleportListener(MoeUtils moe) {
        this.moe = moe;
        if (moe.safePortalConfig.isEnable()) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerPortalEvent e) {
        // 玩家从地狱走门传送到主世界时，会在主世界的边界外死亡
        // 所以需要做的就是，如果玩家在主世界的边界外
        // 就取消传送事件
        if (LocationUtil.outBorder(e.getTo())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(moe.safePortalConfig.msg_cancelled);
            if (moe.safePortalConfig.isDebug()) {
                moe.getLogger().info(String.format(moe.safePortalConfig.msg_debug, e.getPlayer().getName()));
            }
        }
    }

}
