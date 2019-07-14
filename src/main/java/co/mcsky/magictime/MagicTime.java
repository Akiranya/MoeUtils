package co.mcsky.magictime;

import co.mcsky.MoeLib;
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

    /**
     * In millisecond.
     */
    private long lastUsedTime;
    private UUID lastUsedPlayer;
    /**
     * In second.
     */
    private int cooldown;
    private Time status;
    private MoeUtils pl;

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
        return new MagicTime(pl);
    }


    public void setTime(Player player, Time time) {
        /* Check cooldown */
        long now = System.currentTimeMillis(); // In millisecond
        long diff = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (diff <= cooldown) { // Note that cooldown is in second
            // If cooldown is not ready yet, simple returns
            player.sendMessage(String.format(pl.getMoeConfig().GLOBAL_MESSAGE_COOLDOWN, diff));
            return;
        } else {
            lastUsedPlayer = player.getUniqueId();
            lastUsedTime = now; // In millisecond
        }

        /* Check balance */
        int cost = pl.getMoeConfig().MAGICTIME_COST;
        if (!economy.has(player, cost)) {
            player.sendMessage(pl.getMoeConfig().GLOBAL_MESSAGE_NOTENOUGHMONEY);
            return;
        }

        CommandSender console = pl.getServer().getConsoleSender();

        /* Set time */
        String commandSetTime;
        switch (time) {
            case DAY:
                commandSetTime = "essentials:time day all";
                pl.getServer().dispatchCommand(console, commandSetTime);
                pl.getServer().broadcastMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_TIMETODAY);
                status = Time.DAY;
                break;
            case NIGHT:
                commandSetTime = "essentials:time night all";
                pl.getServer().dispatchCommand(console, commandSetTime);
                pl.getServer().broadcastMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_TIMETONIGHT);
                status = Time.NIGHT;
                break;
            default:
                throw new IllegalArgumentException("Unknown state.");
        }

        /* Charge player */
        String commandCharge = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        pl.getServer().dispatchCommand(console, commandCharge);
        player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_COST, cost));

        /* Wait and broadcast */
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            switch (status) {
                case DAY:
                    pl.getServer().broadcastMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_DAYENDED);
                    break;
                case NIGHT:
                    pl.getServer().broadcastMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_NIGHTENDED);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown state.");
            }
        }, MoeLib.toTick(cooldown));
    }

    public void getStatus(Player player) {
        long now = System.currentTimeMillis();
        long waited = TimeUnit.MILLISECONDS.toSeconds(now - lastUsedTime); // In second
        if (waited <= cooldown) {
            String status = pl.getMoeConfig().GLOBAL_MESSAGE_YES;
            String lastUsedPlayer = pl.getServer().getOfflinePlayer(this.lastUsedPlayer).getName();
            int remained = cooldown - (int) waited;
            player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_STATUS, status, lastUsedPlayer, remained));
        } else {
            String status = pl.getMoeConfig().GLOBAL_MESSAGE_NO;
            player.sendMessage(String.format(pl.getMoeConfig().MAGICTIME_MESSAGE_STATUS, status));
        }
    }

    /**
     * This method simply subtracts lastUsedTime by cooldown and reassigns it.
     * @param player The player who runs this command.
     */
    public void reset(Player player) {
        lastUsedTime -= TimeUnit.SECONDS.toMillis(cooldown);
        player.sendMessage(pl.getMoeConfig().MAGICTIME_MESSAGE_TIMERRESET);
    }
}
