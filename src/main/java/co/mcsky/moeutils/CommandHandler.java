package co.mcsky.moeutils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.magicutils.TimeOption;
import co.mcsky.moeutils.magicutils.WeatherOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%moe")
public class CommandHandler extends BaseCommand {

    @Dependency public MoeUtils plugin;
    @Dependency public MagicTime time;
    @Dependency public MagicWeather weather;

    @HelpCommand
    @Description("Show help menu.")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload(CommandSender sender) {
        sender.sendMessage(plugin.getMessage(sender, "common.reloaded",
                                             "time", plugin.reload() + "ms"));
    }

    @Subcommand("version|ver|v")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version(CommandSender sender) {
        sender.sendMessage(plugin.getMessage(sender, "common.version",
                                             "version", plugin.getDescription().getVersion()));
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
