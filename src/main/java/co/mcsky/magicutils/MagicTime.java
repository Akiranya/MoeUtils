package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static co.mcsky.util.MoeLib.toTick;

/**
 * Singleton class.
 */
public class MagicTime extends AMagicUtils {
    private static final String COOLDOWN_KEY = "mt";
    private static MagicTime magicTime = null;
    private UUID lastUsedPlayer; // 最后一次使用魔法时间的玩家

    private MagicTime(MoeUtils moe) {
        super(moe, moe.config.magictime_cooldown);
    }

    public static MagicTime getInstance(MoeUtils moe) {
        if (magicTime == null) return magicTime = new MagicTime(moe);
        return magicTime;
    }

    public void setTime(Player player, ETime time, int cost) {
        if (checkCooldown(player, COOLDOWN_KEY)) return; // 如果玩家冷却还为就绪，直接 return
        if (checkBalance(player, cost)) return; // 如果玩家没有足够的钱，直接 return

        time.setTime(moe); // 改变时间
        String msg = String.format(moe.config.magictime_message_changed, time.getName(moe));
        moe.getServer().broadcastMessage(msg); // 向全服播报
        charge(player, cost); // 向玩家扣费

        use(COOLDOWN_KEY); // 只有完成所有操作才更新冷却时间

        // Store player to map as players should be appeared in output of getStatus()
        lastUsedPlayer = player.getUniqueId();

        // 当事件结束时播报一次
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String serverMsg = String.format(moe.config.magictime_message_ended, time.getName(moe));
            moe.getServer().broadcastMessage(serverMsg);
        }, toTick(COOLDOWN_LENGTH));
    }

    public void getStatus(Player player) {
        // 如果冷却已经就绪，则不输出任何消息
        // 因为 getStatus() 的输出（设定上）只有管理员可以看到
        // 用户体验和提示方便不用考虑太多
        if (!check(COOLDOWN_KEY, COOLDOWN_LENGTH)) { // If COOLDOWN_LENGTH is not ready
            String playerMsg = String.format(moe.config.magictime_message_status,
                    moe.config.global_message_on,
                    moe.getServer().getOfflinePlayer(this.lastUsedPlayer).getName(),
                    remaining(COOLDOWN_KEY, COOLDOWN_LENGTH));
            player.sendMessage(playerMsg);
        }
    }

    public void reset(Player player) {
        reset(COOLDOWN_KEY);
        String playerMsg = String.format(moe.config.global_message_reset, moe.config.magictime_message_prefix);
        player.sendMessage(playerMsg);
    }

}
