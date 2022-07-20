package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.magic.MagicWeather;
import co.mcsky.mewutils.magic.WeatherOption;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandWeather implements Supplier<CommandAPICommand> {
    private final MewUtils plugin;
    private final MagicWeather weather;

    public CommandWeather(MewUtils plugin) {
        this.plugin = plugin;
        this.weather = new MagicWeather();
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("weather")
                .withSubcommand(new CommandAPICommand("sun")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.CLEAR, sender);
                        }))
                .withSubcommand(new CommandAPICommand("rain")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.RAIN, sender);
                        }))
                .withSubcommand(new CommandAPICommand("thunder")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.THUNDER, sender);
                        }))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(weather.getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            weather.resetCooldown();
                            sender.sendMessage(MewUtils.text("common.reset"));
                        }));
    }
}
