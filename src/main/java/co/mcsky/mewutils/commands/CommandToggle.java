package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import co.mcsky.mewutils.foundores.FoundOres;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.function.Supplier;

public class CommandToggle implements Supplier<CommandAPICommand> {

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("toggle")
                .withSubcommand(new CommandAPICommand("mining-notification")
                        .executesPlayer((sender, args) -> {
                            final FoundOres foundOres = MewUtils.p.getFoundOres();
                            if (foundOres.isListener(sender)) {
                                foundOres.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-off"));
                            } else {
                                foundOres.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-on"));
                            }
                        }));
    }
}
