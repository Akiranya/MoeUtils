package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.chat.CustomPrefix;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;

import java.util.function.Supplier;

public class CommandPrefix implements Supplier<CommandAPICommand> {
    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("prefix")
                .withArguments(new GreedyStringArgument("prefix"))
                .executesPlayer((sender, args) -> {
                    String p = (String) args[0];
                    final CustomPrefix prefix = new CustomPrefix();
                    if (p.equalsIgnoreCase("clear")) {
                        prefix.clear(sender);
                    } else {
                        prefix.set(sender, p);
                    }
                });
    }
}
