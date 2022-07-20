package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandReload implements Supplier<CommandAPICommand> {

    private final MewUtils plugin;

    public CommandReload(MewUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("reload")
                .withPermission("mew.admin")
                .executes((sender, args) -> {
                    plugin.reload();
                    sender.sendMessage(MewUtils.text("common.reloaded"));
                });
    }
}
