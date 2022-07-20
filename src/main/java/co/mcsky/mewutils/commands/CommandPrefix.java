package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.chat.CustomPrefix;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;

import java.util.function.Supplier;

public class CommandPrefix implements Supplier<CommandAPICommand> {

    private final MewUtils plugin;
    private final CustomPrefix prefix;

    public CommandPrefix(MewUtils pl) {
        this.plugin = pl;
        this.prefix = new CustomPrefix();
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("prefix")
                .withArguments(new GreedyStringArgument("prefix"))
                .executesPlayer((sender, args) -> {
                    String p = (String) args[0];
                    if (p.equalsIgnoreCase("clear")) {
                        prefix.clear(sender);
                    } else {
                        prefix.set(sender, p);
                    }
                });
    }
}
