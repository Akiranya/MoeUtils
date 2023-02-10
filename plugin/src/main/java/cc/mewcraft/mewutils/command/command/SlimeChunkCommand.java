package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.commandframework.bukkit.parsers.PlayerArgument;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlimeChunkCommand extends AbstractCommand {
    public SlimeChunkCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command<CommandSender> slimechunkCommand = commandManager
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("slimechunk")
            .argument(PlayerArgument.of("player"))
            .handler(context -> {
                Player player = context.get("player");
                if (player.getChunk().isSlimeChunk()) {
                    MewUtils.translations().of("slime_chunk.found").send(player);
                } else {
                    MewUtils.translations().of("slime_chunk.not-found").send(player);
                }
            }).build();
        commandManager.register(slimechunkCommand);
    }
}
