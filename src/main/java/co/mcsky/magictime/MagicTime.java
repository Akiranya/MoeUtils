package co.mcsky.magictime;

import co.mcsky.MoeUtils;
import co.mcsky.utils.Cooldown;
import co.mcsky.utils.MoeLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static co.mcsky.MoeUtils.economy;

/**
 * Singleton class.
 */
public class MagicTime {
    private static MagicTime magicTime = null;

    private MoeUtils moe;
    /**
     * In millisecond.
     */
    private long lastUsedTime;
    private UUID lastUsedPlayer;
    /**
     * In second.
     */
    private int cooldown;
    private int broadcastTask;

    /**
     * Outer world has no access to the constructor.
     */
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


    public void setTime(Player player, TimeType timeType) {
        /* Check cooldown */
        long now = System.currentTimeMillis();
        Cooldown cd = MoeLib.cooldown(now, lastUsedTime, cooldown);
        if (!cd.ready) { // Note that cooldown is in second
            // If cooldown is not ready yet...
            player.sendMessage(
                    String.format(moe.config.global_message_cooldown, cd.remaining)
            );
            return;
        }

        /* Check balance */
        int cost = moe.config.magictime_cost;
        if (!economy.has(player, cost)) {
            player.sendMessage(moe.config.global_message_notenoughmoney);
            return;
        }

        /* Set time */
        timeType.setTime(moe);
        // Broadcast it!
        moe.getServer().broadcastMessage(
                String.format(moe.config.magictime_message_changed, timeType.getName(moe))
        );

        /* Charge player */
        CommandSender console = moe.getServer().getConsoleSender();
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        moe.getServer().dispatchCommand(console, commandCharge);
        player.sendMessage(String.format(moe.config.magictime_message_cost, cost));

        // Only if after all operations do we set lastUsedVariables
        lastUsedPlayer = player.getUniqueId();
        lastUsedTime = now; // In millisecond

        /* Wait and broadcast */
        final String fTimeName = timeType.getName(moe);
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(
                moe, () -> moe.getServer().broadcastMessage(
                        String.format(moe.config.magictime_message_ended, fTimeName)
                ), MoeLib.toTick(cooldown)
        ).getTaskId();
    }

    public void getStatus(Player player) {
        long now = System.currentTimeMillis();
        Cooldown cd = MoeLib.cooldown(now, lastUsedTime, cooldown);
        if (!cd.ready) {
            // If cooldown is not ready yet
            String status = moe.config.global_message_on;
            String lastUsedPlayer = moe.getServer().getOfflinePlayer(this.lastUsedPlayer).getName();
            player.sendMessage(String.format(moe.config.magictime_message_status, status, lastUsedPlayer, cd.remaining));
        } else {
            // If cooldown is ready
            String status = moe.config.global_message_off;
            String none = moe.config.magicweather_message_none;
            player.sendMessage(String.format(moe.config.magictime_message_status, status, none, 0));
        }
    }

    /**
     * This method simply subtracts lastUsedTime by cooldown and reassigns it.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        lastUsedTime -= TimeUnit.SECONDS.toMillis(cooldown);
        player.sendMessage(moe.config.magictime_message_reset);
    }

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }
}
