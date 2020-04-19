package co.mcsky;

import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static co.mcsky.MoeUtils.permission;
import static co.mcsky.magicutils.MagicTime.Time.DAY;
import static co.mcsky.magicutils.MagicTime.Time.NIGHT;
import static co.mcsky.magicutils.MagicWeather.Weather.*;

public class CommandHandler implements TabExecutor {

    private final MoeUtils moe;
    private final MagicTime magicTime;
    private final MagicWeather magicWeather;

    CommandHandler(MoeUtils moe) {
        this.moe = moe;
        Objects.requireNonNull(moe.getCommand("moeutils")).setExecutor(this);
        magicWeather = MagicWeather.getInstance(moe);
        magicTime = MagicTime.getInstance(moe);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) return false;

        /* Reload */
        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(String.format(moe.commonConfig.msg_reloaded, moe.reload()));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(moe.commonConfig.msg_only_player);
            return true;
        }
        Player player = (Player) sender;

        /* MagicTime */
        if (args[0].equalsIgnoreCase("time")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("day")) {
                if (hasPermission(player, "moe.magic.time.day")) {
                    magicTime.setTime(player, DAY, moe.magicTimeConfig.getCost());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (hasPermission(player, "moe.magic.time.night")) {
                    magicTime.setTime(player, NIGHT, moe.magicTimeConfig.getCost());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicTime.reset(player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    magicTime.getStatus(player);
                }
                return true;
            }
        }

        /* MagicWeather */
        if (args[0].equalsIgnoreCase("weather")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("clear")) {
                if (hasPermission(player, "moe.magic.weather.clear")) {
                    magicWeather.setWeather(player, CLEAR, moe.magicWeatherConfig.getCost());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (hasPermission(player, "moe.magic.weather.rain")) {
                    magicWeather.setWeather(player, RAIN, moe.magicWeatherConfig.getCost());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (hasPermission(player, "moe.magic.weather.thunder")) {
                    magicWeather.setWeather(player, THUNDER, moe.magicWeatherConfig.getCost());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    magicWeather.getStatus(player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicWeather.reset(player);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        List<String> result = new ArrayList<>();
        if (strings.length == 1) {
            List<String> completion = List.of("time", "weather", "reload");
            return StringUtil.copyPartialMatches(strings[0], completion, result);
        }
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("time")) {
                List<String> completion = List.of("day", "night", "reset", "status");
                return StringUtil.copyPartialMatches(strings[1], completion, result);
            } else if (strings[0].equalsIgnoreCase("weather")) {
                List<String> completion = List.of("clear", "rain", "thunder", "status", "reset");
                return StringUtil.copyPartialMatches(strings[1], completion, result);
            }
        }
        return null;
    }

    /**
     * Check whether given player has specific permission.
     */
    private boolean hasPermission(CommandSender sender, String perm) {
        if (permission.has(sender, perm)) {
            return true;
        } else { // If does not have permission...
            sender.sendMessage(String.format(moe.commonConfig.msg_noperms, perm));
            return false;
        }
    }
}
