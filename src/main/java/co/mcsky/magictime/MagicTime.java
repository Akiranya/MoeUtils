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

    private MoeUtils pl;
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
    private MagicTime(MoeUtils pl) {
        this.pl = pl;
        cooldown = pl.getMoeConfig().magictime_cooldown; // In second
    }

    public static MagicTime getInstance(MoeUtils pl) {
        if (magicTime != null) {
            return magicTime;
        }
        return magicTime = new MagicTime(pl);
    }


    public void setTime(Player player, TimeType timeType) {
        /* Check cooldown */
        long now = System.currentTimeMillis();
        Cooldown cd = MoeLib.cooldown(now, lastUsedTime, cooldown);
        if (!cd.isReady()) { // Note that cooldown is in second
            // If cooldown is not ready yet...
            player.sendMessage(
                    String.format(pl.getMoeConfig().global_message_cooldown, cd.getRemaining())
            );
            return;
        }

        /* Check balance */
        int cost = pl.getMoeConfig().magictime_cost;
        if (!economy.has(player, cost)) {
            player.sendMessage(pl.getMoeConfig().global_message_notenoughmoney);
            return;
        }


        /* Set time */
        timeType.setTime(pl);
        // Broadcast it!
        pl.getServer().broadcastMessage(
                String.format(pl.getMoeConfig().magictime_message_changed, timeType.getName(pl))
        );

        /* Charge player */
        CommandSender console = pl.getServer().getConsoleSender();
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        pl.getServer().dispatchCommand(console, commandCharge);
        player.sendMessage(String.format(pl.getMoeConfig().magictime_message_cost, cost));

        // Only if after all operations do we set lastUsedVariables
        lastUsedPlayer = player.getUniqueId();
        lastUsedTime = now; // In millisecond

        /* Wait and broadcast */
        final String fTimeName = timeType.getName(pl);
        broadcastTask = Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> pl.getServer().broadcastMessage(
                String.format(pl.getMoeConfig().magictime_message_ended, fTimeName)
        ), MoeLib.toTick(cooldown)).getTaskId();
    }

    public void getStatus(Player player) {
        long now = System.currentTimeMillis();
        long waited = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (waited <= cooldown) {
            String status = pl.getMoeConfig().global_message_on;
            String lastUsedPlayer = pl.getServer().getOfflinePlayer(this.lastUsedPlayer).getName();
            int remained = cooldown - (int) waited;
            player.sendMessage(String.format(pl.getMoeConfig().magictime_message_status, status, lastUsedPlayer, remained));
        } else {
            String status = pl.getMoeConfig().global_message_off;
            String none = pl.getMoeConfig().magicweather_message_none;
            player.sendMessage(String.format(pl.getMoeConfig().magictime_message_status, status, none, 0));
        }
    }

    /**
     * This method simply subtracts lastUsedTime by cooldown and reassigns it.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        lastUsedTime -= TimeUnit.SECONDS.toMillis(cooldown);
        player.sendMessage(pl.getMoeConfig().magictime_message_reset);
    }

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }
}
