package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.magic.MagicTime;
import co.mcsky.mewutils.magic.TimeOption;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandTime implements Supplier<CommandAPICommand> {

    private final MewUtils plugin;
    private final MagicTime time;

    public CommandTime(MewUtils plugin) {
        this.plugin = plugin;
        this.time = new MagicTime();
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("time")
                .withSubcommand(new CommandAPICommand("day")
                        .executesPlayer((sender, args) -> {
                            time.call(TimeOption.DAY, sender);
                        }))
                .withSubcommand(new CommandAPICommand("night")
                        .executesPlayer((sender, args) -> {
                            time.call(TimeOption.NIGHT, sender);
                        }))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(time.getLastPlayer());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            time.resetCooldown();
                            sender.sendMessage(MewUtils.text("common.reset"));
                        }));
    }
}
