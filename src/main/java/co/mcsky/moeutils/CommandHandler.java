package co.mcsky.moeutils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.magicutils.TimeOption;
import co.mcsky.moeutils.magicutils.WeatherOption;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%moe")
public class CommandHandler extends BaseCommand {

    public final MoeUtils plugin;
    public final MagicTime time;
    public final MagicWeather weather;

    ConsoleCommandSender consoleSender;

    public CommandHandler(MoeUtils plugin,
                          MagicTime time,
                          MagicWeather weather) {
        this.plugin = plugin;
        this.time = time;
        this.weather = weather;
    }

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
        getCurrentCommandIssuer().sendMessage(String.format(plugin.getMessage(consoleSender, "common.reloaded"),
                                                            elapsed));
    }

    @Subcommand("version|ver|v")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version() {
        getCurrentCommandIssuer().sendMessage(
                String.format(plugin.getMessage(consoleSender, "common.version"),
                              plugin.getDescription().getVersion()));
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
        public void reset() {
            weather.resetCooldown();
            getCurrentCommandIssuer().sendMessage(plugin.getMessage(consoleSender, "common.reset"));
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic weather.")
        public void status() {
            getCurrentCommandIssuer().sendMessage(weather.getLastPlayers());
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
        public void reset() {
            time.resetCooldown();
            getCurrentCommandIssuer().sendMessage(plugin.getMessage(consoleSender, "common.reset"));
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic time.")
        public void status() {
            getCurrentCommandIssuer().sendMessage(time.getLastPlayer());
        }

    }

}
