package co.mcsky.safeportal;

import co.mcsky.MoeSetting;
import co.mcsky.MoeUtils;
import co.mcsky.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerTeleportListener implements Listener {

    private final MoeUtils moe;
    private final MoeSetting setting;

    public PlayerTeleportListener(MoeUtils moe) {
        this.moe = moe;
        this.setting = MoeSetting.getInstance(moe);
        if (setting.safe_portal.enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerPortalEvent e) {
        final Player player = e.getPlayer();

        // 玩家从地狱走门传送到主世界时，会在主世界的边界外死亡
        // 所以需要做的就是，如果玩家在主世界的边界外
        // 就取消传送事件
        Location to = e.getTo();
        if (LocationUtil.outBorder(to)) {
            e.setCancelled(true);
            player.sendMessage(setting.safe_portal.msg_player);
            if (setting.safe_portal.debug) {
                moe.getLogger().info(String.format(setting.safe_portal.msg_debug, player.getName()));
            }
        }
    }

}
