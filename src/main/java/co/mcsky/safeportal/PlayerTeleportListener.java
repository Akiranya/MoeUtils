package co.mcsky.safeportal;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import static co.mcsky.utils.MoeLib.isOutsideOfBorder;

public class PlayerTeleportListener implements Listener {

    private final MoeUtils moe;

    public PlayerTeleportListener(MoeUtils moe) {
        this.moe = moe;
        if (moe.config.safeportal_on) {
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
        if (isOutsideOfBorder(to)) {
            e.setCancelled(true);
            player.sendMessage(moe.config.safeportal_message_player);
            if (moe.config.safeportal_debug_on) {
                moe.getLogger().info(
                        String.format(moe.config.safeportal_message_debug, player.getName())
                );
            }
        }
    }
}
