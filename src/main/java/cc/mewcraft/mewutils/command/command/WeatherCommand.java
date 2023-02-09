package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import cc.mewcraft.mewutils.magic.WeatherOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand extends AbstractCommand {
    public WeatherCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command.Builder<CommandSender> builder = commandManager
            .commandBuilder("mewutils")
            .permission("mew.magic.weather")
            .literal("weather")
            .senderType(Player.class);

        Command<CommandSender> sunCommand = builder.literal("sun").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            MewUtils.p.getMagicWeather().call(WeatherOption.CLEAR, sender);
        }).build();

        Command<CommandSender> rainCommand = builder.literal("rain").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            MewUtils.p.getMagicWeather().call(WeatherOption.RAIN, sender);
        }).build();

        Command<CommandSender> thunderCommand = builder.literal("thunder").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            MewUtils.p.getMagicWeather().call(WeatherOption.THUNDER, sender);
        }).build();

        Command<CommandSender> statusCommand = builder
            .literal("status")
            .permission("mew.admin")
            .handler(commandContext -> {
                Player sender = (Player) commandContext.getSender();
                sender.sendMessage(MewUtils.p.getMagicWeather().getLastPlayers());
            }).build();

        Command<CommandSender> resetCommand = builder
            .literal("reset")
            .permission("mew.admin")
            .handler(commandContext -> {
                Player player = (Player) commandContext.getSender();
                MewUtils.translations().of("common.reset").send(player);
            }).build();

        commandManager.register(
            sunCommand,
            rainCommand,
            thunderCommand,
            statusCommand,
            resetCommand
        );
    }
}
