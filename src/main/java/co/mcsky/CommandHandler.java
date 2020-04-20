package co.mcsky;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.TimeOption;
import co.mcsky.magicutils.WeatherOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("moeutils|moe|mu")
public class CommandHandler extends BaseCommand {

    private final MoeUtils moe;
    private final MagicTime magicTime;
    private final MagicWeather magicWeather;

    public CommandHandler(MoeUtils moe) {
        this.moe = moe;
        this.magicTime = moe.magicTime;
        this.magicWeather = moe.magicWeather;
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    public void reload(CommandSender sender) {
        sender.sendMessage(String.format(moe.commonCfg.msg_reloaded, moe.reload()));
    }

    @Subcommand("weather")
    @CommandPermission("moe.weather")
    public class MagicWeatherCommand extends BaseCommand {

        @Subcommand("sun|clear")
        @CommandPermission("moe.magic.weather")
        public void callSun(Player player) {
            magicWeather.call(WeatherOption.CLEAR, player);
        }

        @Subcommand("rain|storm")
        @CommandPermission("moe.magic.weather")
        public void callRain(Player player) {
            magicWeather.call(WeatherOption.RAIN, player);
        }

        @Subcommand("thunder|lightning")
        @CommandPermission("moe.magic.weather")
        public void callThunder(Player player) {
            magicWeather.call(WeatherOption.THUNDER, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        public void reset(Player player) {
            magicWeather.resetCooldown();
            player.sendMessage(moe.commonCfg.msg_reset);
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        public void status(Player player) {
            player.sendMessage(magicWeather.getLastPlayers());
        }

    }

    @Subcommand("time")
    public class MagicTimeCommand extends BaseCommand {

        @Subcommand("day")
        @CommandPermission("moe.magic.time")
        public void callDay(Player player) {
            magicTime.call(TimeOption.DAY, player);
        }

        @Subcommand("night")
        @CommandPermission("moe.magic.time")
        public void callNight(Player player) {
            magicTime.call(TimeOption.NIGHT, player);
        }

        @Subcommand("reset")
        @CommandPermission("moe.magic.reset")
        public void reset(Player player) {
            magicTime.resetCooldown();
            player.sendMessage(moe.commonCfg.msg_reset);
        }

        @Subcommand("status")
        @CommandPermission("moe.magic.status")
        public void status(Player player) {
            player.sendMessage(magicTime.getLastPlayer());
        }

    }

}
