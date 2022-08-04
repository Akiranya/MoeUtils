package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandReload implements Supplier<CommandAPICommand> {
    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("reload")
                .withPermission("mew.admin")
                .executes((sender, args) -> {
                    MewUtils.p.reload();
                    sender.sendMessage(MewUtils.text("common.reloaded"));
                });
    }
}
