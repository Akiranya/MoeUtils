package co.mcsky;

import co.mcsky.magictime.MagicTime;
import co.mcsky.magictime.Time;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static co.mcsky.MoeUtils.economy;
import static co.mcsky.MoeUtils.permission;

public class Commands implements CommandExecutor {

    private MoeUtils plugin;

    public Commands(MoeUtils plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("moeutils")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;

        /* Reload */
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getMoeConfig().loadFile();
            plugin.onDisable();
            plugin.onEnable();
            sender.sendMessage(plugin.getMoeConfig().GLOBAL_RELOADED);
            return true;
        }

        // TODO Add message
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        /* MagicTime */
        if (args[0].equalsIgnoreCase("time")) {
            if (args[1].equalsIgnoreCase("day")) {
                if (!hasPermission(player, "moe.magic.time.day")) return true;
                MagicTime.getInstance(plugin).setTime(player, Time.DAY);
            }
            if (args[1].equalsIgnoreCase("night")) {
                if (!hasPermission(player, "moe.magic.time.night")) return true;
                MagicTime.getInstance(plugin).setTime(player, Time.NIGHT);
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (!hasPermission(player, "moe.magic.reset")) return true;
                MagicTime.getInstance(plugin).reset(player);
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (!hasPermission(player, "moe.magic.status")) return true;
                MagicTime.getInstance(plugin).getStatus(player);
            }
        }

        /* MagicWeather */
        if (args[0].equalsIgnoreCase("weather")) {
            if (args[1].equalsIgnoreCase("clear")) {
                if (!hasPermission(player, "moe.magic.weather.clear")) return true;
            }
            if (args[1].equalsIgnoreCase("rain")) {
                if (!hasPermission(player, "moe.magic.weather.rain")) return true;
            }
            if (args[1].equalsIgnoreCase("thunder")) {
                if (!hasPermission(player, "moe.magic.weather.thunder")) return true;
            }
            if (args[1].equalsIgnoreCase("status")) {
                if (!hasPermission(player, "moe.magic.status")) return true;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (!hasPermission(player, "moe.magic.reset")) return true;
            }
        }

        return false;
    }

    private boolean hasPermission(CommandSender sender, String perm) {
        if (!permission.has(sender, perm)) {
            sender.sendMessage(String.format(plugin.getMoeConfig().GLOBAL_MESSAGE_NOPERMS, perm));
            return false;
        }
        return true;
    }
}
