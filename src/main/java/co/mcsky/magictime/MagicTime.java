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
    private static final String COOLDOWNKEY = "magictime";
    private static MagicTime magicTime = null;
    private MoeUtils moe;
    private int cooldown; // In second
    private int broadcastTask; // Id of which broadcasting task is running
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
        // Check cooldown
        if (!Cooldown.getInstance().check(COOLDOWNKEY, cooldown)) {
            // If cooldown is not ready yet...
            String msg = moe.config.global_message_cooldown;
            int remaining = Cooldown.getInstance().remaining(COOLDOWNKEY, cooldown);
            player.sendMessage(String.format(msg, remaining));
            return;
        }

        // Check balance
        if (!economy.has(player, cost)) {
            player.sendMessage(moe.config.global_message_notenoughmoney);
            return;
        }

        // Set time
        timeType.setTime(moe);
        // Broadcast it!
        moe.getServer().broadcastMessage(
                String.format(moe.config.magictime_message_changed, timeType.getName(moe))
        );

        // Charge player
        String cmd = "hamsterecohelper:heh balance take %s %d";
        String commandCharge = String.format(cmd, player.getName(), cost);

        CommandSender console = moe.getServer().getConsoleSender();
        moe.getServer().dispatchCommand(console, commandCharge);

        player.sendMessage(String.format(moe.config.magictime_message_cost, cost));

        // Only if after all operations do we set lastUsedVariables
        try {
            Cooldown.getInstance().use(COOLDOWNKEY);

            // Store UUID of the player,
            // as the player needs to be shown in getStatus() command
            lastUsedPlayer = player.getUniqueId();
        } catch (NullPointerException e) { // Just in case
            moe.getLogger().warning(e.getMessage());
            return;
        }

        // Wait and broadcast
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(moe, () -> {
            String format = String.format(moe.config.magictime_message_ended, timeType.getName(moe));
            moe.getServer().broadcastMessage(format);
        }, toTick(cooldown)).getTaskId();
    }

    public void getStatus(Player player) {
        if (Cooldown.getInstance().check(COOLDOWNKEY, cooldown)) { // If cooldown is ready
            String msg = moe.config.magictime_message_status;
            String status = moe.config.global_message_off;
            String none = moe.config.magicweather_message_none;
            player.sendMessage(String.format(msg, status, none, 0));
        } else {
            String status = moe.config.global_message_on;
            String lastUsedPlayer = moe.getServer().getOfflinePlayer(this.lastUsedPlayer).getName();
            String msg = moe.config.magictime_message_status;
            int remaining = Cooldown.getInstance().remaining(COOLDOWNKEY, cooldown);
            player.sendMessage(String.format(msg, status, lastUsedPlayer, remaining));
        }
    }

    /**
     * Reset cooldown.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        Cooldown.getInstance().reset(COOLDOWNKEY);
        player.sendMessage(moe.config.magictime_message_reset);
    }

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }
}
