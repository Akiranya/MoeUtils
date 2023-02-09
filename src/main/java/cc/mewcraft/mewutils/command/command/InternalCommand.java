package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import org.bukkit.command.CommandSender;

public class InternalCommand extends AbstractCommand {
    public InternalCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command<CommandSender> reloadCommand = commandManager
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("reload")
            .handler(context -> {
                CommandSender sender = context.getSender();
                MewUtils.p.reload();
                MewUtils.translations().of("common.reloaded").send(sender);
            }).build();
        commandManager.register(reloadCommand);
    }
}
