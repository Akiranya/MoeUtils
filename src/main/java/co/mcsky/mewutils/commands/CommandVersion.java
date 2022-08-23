package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandVersion implements Supplier<CommandAPICommand> {
    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("version")
                .withPermission("mew.admin")
                .executes((sender, args) -> {
                    sender.sendMessage(MewUtils.text("common.version", "version", MewUtils.p.getDescription().getVersion()));
                });
    }
}