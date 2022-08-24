package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class CommandSlimeChunk implements Supplier<CommandAPICommand> {

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("slimechunk")
                .withPermission("mew.admin")
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .executes((sender, args) -> {
                    var player = (Player) args[0];
                    if (player.getChunk().isSlimeChunk()) {
                        player.sendMessage(MewUtils.text("slime-chunk.found"));
                    } else {
                        player.sendMessage(MewUtils.text("slime-chunk.not-found"));
                    }
                });
    }

}
