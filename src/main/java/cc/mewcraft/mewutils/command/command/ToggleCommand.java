package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.announceore.FoundOres;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCommand extends AbstractCommand {
    public ToggleCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command<CommandSender> toggleCommand = commandManager
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("toggle")
            .senderType(Player.class)
            .handler(commandContext -> {
                Player sender = (Player) commandContext.getSender();
                FoundOres oreAnnouncer = MewUtils.p.getFoundOres();
                if (oreAnnouncer.isListener(sender)) {
                    oreAnnouncer.toggleBroadcast(sender);
                    sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-off"));
                } else {
                    oreAnnouncer.toggleBroadcast(sender);
                    sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-on"));
                }
            }).build();
        commandManager.register(toggleCommand);
    }
}
