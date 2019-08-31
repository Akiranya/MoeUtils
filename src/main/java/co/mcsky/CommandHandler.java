package co.mcsky;

import co.mcsky.magictime.MagicTime;
import co.mcsky.magictime.TimeType;
import co.mcsky.magicweather.MagicWeather;
import co.mcsky.magicweather.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static co.mcsky.MoeUtils.permission;

public class CommandHandler implements CommandExecutor {

    private final MoeUtils moe;
    private final MagicTime magicTime;
    private final MagicWeather magicWeather;

    public CommandHandler(MoeUtils moe) {
        this.moe = moe;
        Objects.requireNonNull(moe.getCommand("moeutils")).setExecutor(this);
        magicWeather = MagicWeather.getInstance(moe);
        magicTime = MagicTime.getInstance(moe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;

        /* Reload */
        if (args[0].equalsIgnoreCase("reload")) {
            moe.config.reloadConfig();
            moe.onDisable();
            moe.onEnable();
            sender.sendMessage(moe.config.global_message_reloaded);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(moe.config.global_message_playeronly);
            return true;
        }
        Player player = (Player) sender;

        /* MagicTime */
        if (args[0].equalsIgnoreCase("time")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("day")) {
                if (hasPermission(player, "moe.magic.time.day")) {
                    magicTime.setTime(player, TimeType.DAY, moe.config.magictime_cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (hasPermission(player, "moe.magic.time.night")) {
                    magicTime.setTime(player, TimeType.NIGHT, moe.config.magictime_cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicTime.reset(player);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    magicTime.getStatus(player);
                    return true;
                }
            }
        }

        /* MagicWeather */
        if (args[0].equalsIgnoreCase("weather")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("clear")) {
                if (hasPermission(player, "moe.magic.weather.clear")) {
                    magicWeather.setWeather(player, WeatherType.CLEAR, moe.config.magicweather_cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (hasPermission(player, "moe.magic.weather.rain")) {
                    magicWeather.setWeather(player, WeatherType.RAIN, moe.config.magicweather_cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (hasPermission(player, "moe.magic.weather.thunder")) {
                    magicWeather.setWeather(player, WeatherType.THUNDER, moe.config.magicweather_cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    magicWeather.getStatus(player);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicWeather.reset(player, player.getWorld().getName());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether given player has specific permission.
     *
     * @param sender The player.
     * @param perm   The permission node.
     * @return Whether given player has specific permission.
     */
    private boolean hasPermission(CommandSender sender, String perm) {
        if (permission.has(sender, perm)) return true;
        else { // If does not have permission...
            sender.sendMessage(String.format(moe.config.global_message_noperms, perm));
            return false;
        }
    }
}
