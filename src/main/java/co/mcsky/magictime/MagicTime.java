package co.mcsky.magictime;

import co.mcsky.utils.Cooldown;
import co.mcsky.utils.MoeLib;
import co.mcsky.MoeUtils;
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
        cooldown = pl.getMoeConfig().MAGICTIME_COOLDOWN; // In second
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
                    String.format(pl.getMoeConfig().GLOBAL_MESSAGE_COOLDOWN, cd.getRemaining())
            );
            return;
        }

        /* Check balance */
        int cost = pl.getMoeConfig().MAGICTIME_COST;
        if (!economy.has(player, cost)) {
            player.sendMessage(pl.getMoeConfig().GLOBAL_MESSAGE_NOTENOUGHMONEY);
            return;
        }


        /* Set time */
        timeType.setTime(pl);
        // Broadcast it!
        pl.getServer().broadcastMessage(
                String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_CHANGED, timeType.getName(pl))
        );

        /* Charge player */
        CommandSender console = pl.getServer().getConsoleSender();
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        pl.getServer().dispatchCommand(console, commandCharge);
        player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_COST, cost));

        // Only if after all operations do we set lastUsedVariables
        lastUsedPlayer = player.getUniqueId();
        lastUsedTime = now; // In millisecond

        /* Wait and broadcast */
        broadcastTask = Bukkit.getScheduler().runTaskLater(pl, () -> pl.getServer().broadcastMessage(
                String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_ENDED, timeType.getName(pl))
        ), MoeLib.toTick(cooldown)).getTaskId();
    }

    public void getStatus(Player player) {
        long now = System.currentTimeMillis();
        long waited = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (waited <= cooldown) {
            String status = pl.getMoeConfig().GLOBAL_MESSAGE_ON;
            String lastUsedPlayer = pl.getServer().getOfflinePlayer(this.lastUsedPlayer).getName();
            int remained = cooldown - (int) waited;
            player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_STATUS, status, lastUsedPlayer, remained));
        } else {
            String status = pl.getMoeConfig().GLOBAL_MESSAGE_OFF;
            String none = pl.getMoeConfig().MAGICWEATHER_MESSAGE_NONE;
            player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_STATUS, status, none, 0));
        }
    }

    /**
     * This method simply subtracts lastUsedTime by cooldown and reassigns it.
     *
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        lastUsedTime -= TimeUnit.SECONDS.toMillis(cooldown);
        player.sendMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_RESET);
    }

    public void cancelBroadcastTask() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }
}
