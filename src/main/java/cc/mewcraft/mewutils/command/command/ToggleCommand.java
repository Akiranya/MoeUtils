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
                Player player = (Player) commandContext.getSender();
                FoundOres oreAnnouncer = MewUtils.p.getFoundOres();
                if (oreAnnouncer.isListener(player.getUniqueId())) {
                    oreAnnouncer.toggleBroadcast(player.getUniqueId());
                    MewUtils.translations().of("found_ores.toggle-broadcast-off").send(player);
                } else {
                    oreAnnouncer.toggleBroadcast(player.getUniqueId());
                    MewUtils.translations().of("found_ores.toggle-broadcast-on").send(player);
                }
            }).build();
        commandManager.register(toggleCommand);
    }
}
