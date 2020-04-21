package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.TimeOption;
import co.mcsky.magicutils.WeatherOption;
import org.bukkit.entity.Player;

@CommandAlias("%moe")
public class MagicUtilsCommand extends BaseCommand {

    @Dependency
    private MoeUtils moe;
    @Dependency
    private LanguageManager lm;
    @Dependency
    private MagicWeather magicWeather;
    @Dependency
    private MagicTime magicTime;

    @Subcommand("weather")
    public class WeatherCommand extends BaseCommand {

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
            getCurrentCommandIssuer().sendMessage(lm.common_reset);
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
            getCurrentCommandIssuer().sendMessage(lm.common_reset);
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        @Description("Get the last player who called magic time.")
        public void status() {
            getCurrentCommandIssuer().sendMessage(magicTime.getLastPlayer());
        }

    }


}
