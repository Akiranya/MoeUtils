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
                if (!hasPermission(player, "moe.magic.time.day")) return true;
                magicTime.setTime(player, TimeType.DAY);
                return true;
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (!hasPermission(player, "moe.magic.time.night")) return true;
                magicTime.setTime(player, TimeType.NIGHT);
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (!hasPermission(player, "moe.magic.reset")) return true;
                magicTime.reset(player);
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (!hasPermission(player, "moe.magic.status")) return true;
                magicTime.getStatus(player);
                return true;
            }
        }

        /* MagicWeather */
        if (args[0].equalsIgnoreCase("weather")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("clear")) {
                if (!hasPermission(player, "moe.magic.weather.clear")) return true;
                magicWeather.setWeather(player, WeatherType.CLEAR);
                return true;
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (!hasPermission(player, "moe.magic.weather.rain")) return true;
                magicWeather.setWeather(player, WeatherType.RAIN);
                return true;
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (!hasPermission(player, "moe.magic.weather.thunder")) return true;
                magicWeather.setWeather(player, WeatherType.THUNDER);
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (!hasPermission(player, "moe.magic.status")) return true;
                magicWeather.getStatus(player);
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (!hasPermission(player, "moe.magic.reset")) return true;
                magicWeather.reset(player, player.getWorld().getName());
                return true;
            }
        }

        return false;
    }

    private boolean hasPermission(CommandSender sender, String perm) {
        if (!permission.has(sender, perm)) {
            sender.sendMessage(String.format(moe.config.global_message_noperms, perm));
            return false;
        }
        return true;
    }
}
