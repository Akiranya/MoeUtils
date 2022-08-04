package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.magic.MagicWeather;
import co.mcsky.mewutils.magic.WeatherOption;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandWeather implements Supplier<CommandAPICommand> {
    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("weather")
                .withSubcommand(new CommandAPICommand("sun")
                        .executesPlayer((sender, args) -> {
                            MewUtils.p.getMagicWeather().call(WeatherOption.CLEAR, sender);
                        }))
                .withSubcommand(new CommandAPICommand("rain")
                        .executesPlayer((sender, args) -> {
                            MewUtils.p.getMagicWeather().call(WeatherOption.RAIN, sender);
                        }))
                .withSubcommand(new CommandAPICommand("thunder")
                        .executesPlayer((sender, args) -> {
                            MewUtils.p.getMagicWeather().call(WeatherOption.THUNDER, sender);
                        }))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(MewUtils.p.getMagicWeather().getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            MewUtils.p.getMagicWeather().resetCooldown();
                            sender.sendMessage(MewUtils.text("common.reset"));
                        }));
    }
}
