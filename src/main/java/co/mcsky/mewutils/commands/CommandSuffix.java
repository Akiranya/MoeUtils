package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.chat.CustomSuffix;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;

import java.util.function.Supplier;

public class CommandSuffix implements Supplier<CommandAPICommand> {

    private final MewUtils plugin;
    private final CustomSuffix suffix;

    public CommandSuffix(MewUtils plugin) {
        this.plugin = plugin;
        this.suffix = new CustomSuffix();
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("suffix")
                .withArguments(new GreedyStringArgument("suffix"))
                .executesPlayer((sender, args) -> {
                    String p = (String) args[0];
                    if (p.equalsIgnoreCase("clear")) {
                        suffix.clear(sender);
                    } else {
                        suffix.set(sender, p);
                    }
                });
    }
}
