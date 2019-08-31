package co.mcsky.magictime;

import co.mcsky.MoeUtils;
import co.mcsky.utils.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static co.mcsky.MoeUtils.economy;
import static co.mcsky.utils.MoeLib.toTick;

/**
 * Singleton class.
 */
public class MagicTime {
    private static final String COOLDOWN_KEY = "magictime";
    private static MagicTime magicTime = null;
    private final MoeUtils moe;
    private final int cooldown; // In second
    private UUID lastUsedPlayer; // Player who lastly used MagicTime, i.e. ran command

    private MagicTime(MoeUtils moe) {
        this.moe = moe;
        cooldown = moe.config.magictime_cooldown; // In second
    }

    public static MagicTime getInstance(MoeUtils moe) {
        if (magicTime != null) {
            return magicTime;
        }
        return magicTime = new MagicTime(moe);
    }

    public void setTime(Player player, TimeType timeType, int cost) {
        // 如果玩家冷却还为就绪，直接 return
        if (!checkCooldown(player)) return;

        // 如果玩家没有足够的钱，直接 return
        if (!checkBalance(player, cost)) return;

        // Set time
        timeType.setTime(moe);
        // Broadcast the event
        String msg = String.format(
                moe.config.magictime_message_changed,
                timeType.getName(moe));
        moe.getServer().broadcastMessage(msg);

        // Charge player
        chargePlayer(player, cost);

        String playerMsg = String.format(
                moe.config.magictime_message_cost,
                cost);
        player.sendMessage(playerMsg);

        try {
            // Only if after all operations do we set lastUsed variables
            Cooldown.getInstance().use(COOLDOWN_KEY);

            // Store UUID of the player to map,
            // as the player needs to be shown in getStatus() command
            lastUsedPlayer = player.getUniqueId();
        } catch (NullPointerException e) { // Just in case
            moe.getLogger().warning(e.getMessage());
            return;
        }

        // Broadcast the end of event after given duration
        Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String serverMsg = String.format(
                    moe.config.magictime_message_ended,
                    timeType.getName(moe));
            moe.getServer().broadcastMessage(serverMsg);
        }, toTick(cooldown));
    }

    public void getStatus(Player player) {
        // 如果冷却已经就绪，则不输出任何消息
        // 因为 getStatus() 的输出（设定上）只有管理员可以看到
        // 用户体验和提示方便不用考虑太多
        if (!Cooldown.getInstance().check(COOLDOWN_KEY, cooldown)) { // If cooldown is not ready
            String playerMsg = String.format(
                    moe.config.magictime_message_status,
                    moe.config.global_message_on,
                    moe.getServer().getOfflinePlayer(this.lastUsedPlayer).getName(),
                    Cooldown.getInstance().remaining(COOLDOWN_KEY, cooldown));
            player.sendMessage(playerMsg);
        }
    }

    /**
     * Reset cooldown.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        Cooldown.getInstance().reset(COOLDOWN_KEY);
        player.sendMessage(moe.config.magictime_message_reset);
    }

    private boolean checkCooldown(Player player) {
        if (Cooldown.getInstance().check(COOLDOWN_KEY, cooldown)) {
            return true;
        }
        // If cooldown is not ready yet...
        String msg = moe.config.global_message_cooldown;
        int remaining = Cooldown.getInstance().remaining(COOLDOWN_KEY, cooldown);
        player.sendMessage(String.format(msg, remaining));
        return false;
    }

    private boolean checkBalance(Player player, int cost) {
        if (economy.has(player, cost)) {
            return true;
        }
        player.sendMessage(moe.config.global_message_notenoughmoney);
        return false;
    }

    private void chargePlayer(Player player, int cost) {
        String cmd = "hamsterecohelper:heh balance take %s %d";
        String cmdCharge = String.format(cmd, player.getName(), cost);
        CommandSender console = moe.getServer().getConsoleSender();
        moe.getServer().dispatchCommand(console, cmdCharge); // This command CHARGES player
    }

}
