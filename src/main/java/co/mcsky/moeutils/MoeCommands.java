package co.mcsky.moeutils;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.chat.CustomPrefix;
import co.mcsky.moeutils.chat.CustomSuffix;
import co.mcsky.moeutils.data.DataSource;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.magic.TimeOption;
import co.mcsky.moeutils.magic.WeatherOption;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mu")
public class MoeCommands extends BaseCommand {

    @Dependency
    public MoeUtils plugin;
    @Dependency
    public DataSource dataSource;
    @Dependency
    public MagicTime time;
    @Dependency
    public MagicWeather weather;
    @Dependency
    public CustomPrefix customPrefix;
    @Dependency
    public CustomSuffix customSuffix;

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload(CommandSender sender) {
        MoeUtils.plugin.reload();
        sender.sendMessage(plugin.getMessage(sender, "common.reloaded"));
    }

    @Subcommand("version|ver")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version(CommandSender sender) {
        sender.sendMessage(plugin.getMessage(sender, "common.version", "version", plugin.getDescription().getVersion()));
    }

    @Subcommand("prefix")
    @CommandCompletion("@nothing")
    @CommandPermission("moe.prefix")
    public void prefix(Player player, String prefix) {
        customPrefix.set(player, prefix);
    }

    @Subcommand("prefix clear")
    @CommandPermission("moe.prefix")
    public void prefix(Player player) {
        customPrefix.clear(player);
    }

    @Subcommand("suffix")
    @CommandCompletion("@nothing")
    @CommandPermission("moe.suffix")
    public void suffix(Player player, String suffix) {
        customSuffix.set(player, suffix);
    }

    @Subcommand("suffix clear")
    @CommandPermission("moe.suffix")
    public void suffix(Player player) {
        customSuffix.clear(player);
    }

    @Subcommand("portal-changer")
    @CommandPermission("moe.admin")
    public class PortalChangerCommand extends BaseCommand {
        @Subcommand("set")
        public void set(Player player) {
            final Location location = player.getLocation().toBlockLocation();
            dataSource.addEndEyeTargetLocation(location);
            player.sendMessage(plugin.getMessage(player, "end-eye-changer.set", "location", ACFBukkitUtil.blockLocationToString(location)));
        }

        @Subcommand("list")
        public void list(CommandSender sender) {
            sender.sendMessage(plugin.getMessage(sender, "end-eye-changer.list-title"));
            dataSource.getEndEyeTargetLocations().forEach(location -> sender.sendMessage(plugin.getMessage(sender, "end-eye-changer.list", "location", ACFBukkitUtil.blockLocationToString(location))));
        }

        @Subcommand("clear")
        public void clear(CommandSender sender) {
            dataSource.clearTargetLocations();
            sender.sendMessage(plugin.getMessage(sender, "end-eye-changer.clear"));
        }
    }

    @Subcommand("weather")
    public class WeatherCommand extends BaseCommand {

        @Subcommand("sun|clear")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic clear.")
        public void callSun(Player player) {
            weather.call(WeatherOption.CLEAR, player);
        }

        @Subcommand("rain|storm")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic rain.")
        public void callRain(Player player) {
            weather.call(WeatherOption.RAIN, player);
        }

        @Subcommand("thunder|lightning")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic thunder.")
        public void callThunder(Player player) {
            weather.call(WeatherOption.THUNDER, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        @Description("Reset cooldown of magic weather.")
        public void reset(CommandSender sender) {
            weather.resetCooldown();
            sender.sendMessage(plugin.getMessage(sender, "common.reset"));
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic weather.")
        public void status(CommandSender sender) {
            sender.sendMessage(weather.getLastPlayers());
        }

    }

    @Subcommand("time")
    public class TimeCommand extends BaseCommand {

        @Subcommand("day")
        @CommandPermission("moe.magic.time")
        @Description("Call magic day.")
        public void callDay(Player player) {
            time.call(TimeOption.DAY, player);
        }

        @Subcommand("night")
        @CommandPermission("moe.magic.time")
        @Description("Call magic night.")
        public void callNight(Player player) {
            time.call(TimeOption.NIGHT, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        @Description("Reset cooldown of magic time.")
        public void reset(CommandSender sender) {
            time.resetCooldown();
            sender.sendMessage(plugin.getMessage(sender, "common.reset"));
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic time.")
        public void status(CommandSender sender) {
            sender.sendMessage(time.getLastPlayer());
        }

    }

}
