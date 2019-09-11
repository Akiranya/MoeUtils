package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MagicTime extends AMagicUtils<UUID> {
    private static final UUID COOLDOWN_KEY = UUID.randomUUID();
    private static MagicTime magicTime = null;
    private UUID lastUsedPlayer; // 最后一次使用魔法时间的玩家

    private MagicTime(MoeUtils moe) {
        super(moe, moe.setting.magic_time.cooldown);
    }

    public static MagicTime getInstance(MoeUtils moe) {
        if (magicTime == null) return magicTime = new MagicTime(moe);
        return magicTime;
    }

    public void setTime(Player player, Time time, int cost) {
        if (checkCooldown(player, COOLDOWN_KEY) || checkBalance(player, cost)) return;
        time.setTime(moe); // 改变时间
        String broadcast = String.format(moe.setting.magic_time.msg_changed, time.getDisplayName(moe));
        moe.getServer().broadcastMessage(broadcast); // 向全服播报
        charge(player, cost); // 向玩家扣费
        use(COOLDOWN_KEY); // 更新冷却时间（只有以上操作全部完成时才更新冷却时间）
        lastUsedPlayer = player.getUniqueId(); // 更新最后一次使用魔法时间的玩家
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String serverMsg = String.format(moe.setting.magic_time.msg_ended, time.getDisplayName(moe));
            moe.getServer().broadcastMessage(serverMsg); // 当事件结束时播报一次
        }, TimeUtil.toTick(COOLDOWN_LENGTH));
    }

    public void getStatus(Player player) {
        // 如果冷却已经就绪，则不输出任何消息
        // 因为 getStatus() 的输出（设定上）只有管理员可以看到
        // 用户体验和提示方便不用考虑太多
        if (!check(COOLDOWN_KEY, COOLDOWN_LENGTH)) { // If COOLDOWN_LENGTH is not ready
            String playerMsg = String.format(moe.setting.magic_time.msg_status,
                    moe.setting.globe.msg_on,
                    moe.getServer().getOfflinePlayer(this.lastUsedPlayer).getName(),
                    remaining(COOLDOWN_KEY, COOLDOWN_LENGTH));
            player.sendMessage(playerMsg);
        }
    }

    public void reset(Player player) {
        reset(COOLDOWN_KEY);
        String playerMsg = String.format(moe.setting.globe.msg_reset, moe.setting.magic_time.msg_prefix);
        player.sendMessage(playerMsg);
    }

    public enum Time {
        DAY("day"),
        NIGHT("night");

        private final String commandName;

        Time(String cmdArg) {
            this.commandName = cmdArg;
        }

        public String getDisplayName(MoeUtils moe) {
            switch (this) {
                case DAY:
                    return moe.setting.magic_time.msg_day;
                case NIGHT:
                    return moe.setting.magic_time.msg_night;
                default:
                    throw new IllegalStateException("Unknown time value.");
            }
        }

        public String getCmdArg() {
            return commandName;
        }

        public void setTime(MoeUtils moe) {
            CommandSender console = moe.getServer().getConsoleSender();
            String command = String.format("essentials:time %s all", getCmdArg());
            moe.getServer().dispatchCommand(console, command);
        }
    }

}
