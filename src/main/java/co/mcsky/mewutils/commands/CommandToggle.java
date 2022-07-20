package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.foundores.FoundOres;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandToggle implements Supplier<CommandAPICommand> {
    private final MewUtils plugin;
    private final FoundOres ores;

    public CommandToggle(MewUtils plugin) {
        this.plugin = plugin;
        this.ores = new FoundOres();
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("toggle")
                .withSubcommand(new CommandAPICommand("mining-notification")
                        .executesPlayer((sender, args) -> {
                            if (ores.isListener(sender)) {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-off"));
                            } else {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-on"));
                            }
                        }));
    }
}
