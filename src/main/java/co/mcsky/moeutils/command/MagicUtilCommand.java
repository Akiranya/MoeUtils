package co.mcsky.moeutils.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.magic.TimeOption;
import co.mcsky.moeutils.magic.WeatherOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%main")
public class MagicUtilCommand extends BaseCommand {
    @Dependency
    public MagicTime time;
    @Dependency
    public MagicWeather weather;

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
            sender.sendMessage(MoeUtils.text("common.reset"));
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
            sender.sendMessage(MoeUtils.text("common.reset"));
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic time.")
        public void status(CommandSender sender) {
            sender.sendMessage(time.getLastPlayer());
        }

    }
}
