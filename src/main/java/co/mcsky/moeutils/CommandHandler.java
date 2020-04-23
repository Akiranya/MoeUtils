package co.mcsky.moeutils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.magicutils.TimeOption;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.magicutils.WeatherOption;
import org.bukkit.entity.Player;

import static co.mcsky.moeutils.MoeUtils.plugin;

@CommandAlias("%moe")
public class CommandHandler extends BaseCommand {

    @HelpCommand
    @Description("Show help menu.")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload() {
        long elapsed = plugin.reload();
        getCurrentCommandIssuer().sendMessage(String.format(plugin.lang.common_reloaded, elapsed));
    }

    @Subcommand("version|ver|v")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version() {
        getCurrentCommandIssuer().sendMessage(
                String.format(plugin.lang.common_version, plugin.getDescription().getVersion()));
    }

    @Subcommand("weather")
    public class WeatherCommand extends BaseCommand {

        @Dependency
        private MagicWeather magicWeather;

        @Subcommand("sun|clear")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic clear.")
        public void callSun(Player player) {
            magicWeather.call(WeatherOption.CLEAR, player);
        }

        @Subcommand("rain|storm")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic rain.")
        public void callRain(Player player) {
            magicWeather.call(WeatherOption.RAIN, player);
        }

        @Subcommand("thunder|lightning")
        @CommandPermission("moe.magic.weather")
        @Description("Call magic thunder.")
        public void callThunder(Player player) {
            magicWeather.call(WeatherOption.THUNDER, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        @Description("Reset cooldown of magic weather.")
        public void reset() {
            magicWeather.resetCooldown();
            getCurrentCommandIssuer().sendMessage(plugin.lang.common_reset);
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic weather.")
        public void status() {
            getCurrentCommandIssuer().sendMessage(magicWeather.getLastPlayers());
        }

    }

    @Subcommand("time")
    public class TimeCommand extends BaseCommand {

        @Dependency
        private MagicTime magicTime;

        @Subcommand("day")
        @CommandPermission("moe.magic.time")
        @Description("Call magic day.")
        public void callDay(Player player) {
            magicTime.call(TimeOption.DAY, player);
        }

        @Subcommand("night")
        @CommandPermission("moe.magic.time")
        @Description("Call magic night.")
        public void callNight(Player player) {
            magicTime.call(TimeOption.NIGHT, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        @Description("Reset cooldown of magic time.")
        public void reset() {
            magicTime.resetCooldown();
            getCurrentCommandIssuer().sendMessage(plugin.lang.common_reset);
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic time.")
        public void status() {
            getCurrentCommandIssuer().sendMessage(magicTime.getLastPlayer());
        }

    }

}
