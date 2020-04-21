package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.magicutils.WeatherOption;
import org.bukkit.entity.Player;

@CommandAlias("mu|moe|moeutils")
public class MagicWeatherCommand extends BaseCommand {

    private final MoeUtils moe;
    private final LanguageManager lm;
    private final MagicWeather magicWeather;

    public MagicWeatherCommand(MoeUtils moe) {
        this.moe = moe;
        this.lm = moe.languageManager;
        this.magicWeather = new MagicWeather(moe);
    }

    @Subcommand("weather sun|clear")
    @CommandPermission("moe.magic.weather")
    @Description("Call magic clear.")
    public void callSun(Player player) {
        magicWeather.call(WeatherOption.CLEAR, player);
    }

    @Subcommand("weather rain|storm")
    @CommandPermission("moe.magic.weather")
    @Description("Call magic rain.")
    public void callRain(Player player) {
        magicWeather.call(WeatherOption.RAIN, player);
    }

    @Subcommand("weather thunder|lightning")
    @CommandPermission("moe.magic.weather")
    @Description("Call magic thunder.")
    public void callThunder(Player player) {
        magicWeather.call(WeatherOption.THUNDER, player);
    }

    @Subcommand("weather reset")
    @CommandPermission("moe.magic.reset")
    @Description("Reset cooldown of magic weather.")
    public void reset(Player player) {
        magicWeather.resetCooldown();
        player.sendMessage(lm.common_reset);
    }

    @Subcommand("weather status")
    @CommandPermission("moe.magic.status")
    @Description("Get the last player who called magic weather.")
    public void status(Player player) {
        player.sendMessage(magicWeather.getLastPlayers());
    }

}
