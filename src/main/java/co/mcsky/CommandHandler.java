package co.mcsky;

import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.TimeOption;
import co.mcsky.magicutils.WeatherOption;
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

public class CommandHandler implements TabExecutor {

    private final MoeUtils moe;
    private final MagicTime magicTime;
    private final MagicWeather magicWeather;

    CommandHandler(MoeUtils moe,
                   MagicTime magicTime,
                   MagicWeather magicWeather) {
        this.moe = moe;
        this.magicTime = magicTime;
        this.magicWeather = magicWeather;
        Objects.requireNonNull(moe.getCommand("moeutils")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) return false;
        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(String.format(moe.commonCfg.msg_reloaded, moe.reload()));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(moe.commonCfg.msg_only_player);
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("time")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("day")) {
                if (hasPermission(player, "moe.magic.time.day")) {
                    magicTime.call(TimeOption.DAY, player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (hasPermission(player, "moe.magic.time.night")) {
                    magicTime.call(TimeOption.NIGHT, player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    player.sendMessage(magicTime.getLastPlayer());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicTime.resetCooldown();
                    sender.sendMessage(moe.commonCfg.msg_reset);
                }
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("weather")) {
            if (args.length != 2) return false;
            if (args[1].equalsIgnoreCase("clear")) {
                if (hasPermission(player, "moe.magic.weather.clear")) {
                    magicWeather.call(WeatherOption.CLEAR, player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (hasPermission(player, "moe.magic.weather.rain")) {
                    magicWeather.call(WeatherOption.RAIN, player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (hasPermission(player, "moe.magic.weather.thunder")) {
                    magicWeather.call(WeatherOption.THUNDER, player);
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (hasPermission(player, "moe.magic.status")) {
                    sender.sendMessage(magicWeather.getLastPlayers());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (hasPermission(player, "moe.magic.reset")) {
                    magicWeather.resetCooldown();
                    sender.sendMessage(moe.commonCfg.msg_reset);
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
     * Check if the {@code sender} has specific permission. If not, it will send
     * relevant messages to the {@code sender}. Otherwise, nothing will be sent
     * to the {@code sender}.
     *
     * @param sender The player related to this permission check
     * @param perm   The permission to be checked for
     *
     * @return True if the sender has the permission. False else wise.
     */
    private boolean hasPermission(CommandSender sender, String perm) {
        if (permission.has(sender, perm)) {
            return true;
        }
        sender.sendMessage(String.format(moe.commonCfg.msg_noperms, perm));
        return false;
    }

}
