package co.mcsky;

import co.mcsky.magicutils.ETime;
import co.mcsky.magicutils.EWeather;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static co.mcsky.MoeUtils.permission;

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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;

        /* Reload */
        if (args[0].equalsIgnoreCase("reload")) {
            moe.setting.reloadConfig();
            moe.onDisable();
            moe.onEnable();
            sender.sendMessage(moe.setting.globe.msg_reload);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(moe.setting.globe.msg_player_only);
            return true;
        }
        Player player = (Player) sender;

        /* MagicTime */
        if (args[0].equalsIgnoreCase("time")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("day")) {
                if (hasPermission(player, "moe.magic.time.day")) {
                    magicTime.setTime(player, ETime.DAY, moe.setting.magic_time.cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (hasPermission(player, "moe.magic.time.night")) {
                    magicTime.setTime(player, ETime.NIGHT, moe.setting.magic_time.cost);
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
                    magicWeather.setWeather(player, EWeather.CLEAR, moe.setting.magic_weather.cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (hasPermission(player, "moe.magic.weather.rain")) {
                    magicWeather.setWeather(player, EWeather.RAIN, moe.setting.magic_weather.cost);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (hasPermission(player, "moe.magic.weather.thunder")) {
                    magicWeather.setWeather(player, EWeather.THUNDER, moe.setting.magic_weather.cost);
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
                    magicWeather.reset(player);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
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
            sender.sendMessage(String.format(moe.setting.globe.msg_no_perm, perm));
            return false;
        }
    }
}
