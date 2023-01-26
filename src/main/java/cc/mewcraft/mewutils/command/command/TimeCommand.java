package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import cc.mewcraft.mewutils.magic.TimeOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand extends AbstractCommand {
    public TimeCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command.Builder<CommandSender> builder = commandManager
            .commandBuilder("mewutils")
            .permission("mew.magic.time")
            .literal("time")
            .senderType(Player.class);

        Command<CommandSender> dayCommand = builder.literal("day").handler(context -> {
            Player sender = (Player) context.getSender();
            MewUtils.p.getMagicTime().call(TimeOption.DAY, sender);
        }).build();

        Command<CommandSender> nightCommand = builder.literal("night").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            MewUtils.p.getMagicTime().call(TimeOption.NIGHT, sender);
        }).build();

        Command<CommandSender> statusCommand = builder.literal("status").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            sender.sendMessage(MewUtils.p.getMagicTime().getLastPlayer());
        }).build();

        Command<CommandSender> resetCommand = builder.literal("reset").handler(commandContext -> {
            Player sender = (Player) commandContext.getSender();
            MewUtils.p.getMagicTime().resetCooldown();
            sender.sendMessage(MewUtils.text("common.reset"));
        }).build();

        commandManager.register(dayCommand, nightCommand, statusCommand, resetCommand);
    }
}
