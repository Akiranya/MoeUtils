package co.mcsky.mewutils;

import co.mcsky.mewutils.commands.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

public class MewCommands {

    private static final String COMMAND_NAME = "mewutils";

    public void register() {
        final CommandAPICommand mu = new CommandAPICommand(COMMAND_NAME)
                .withAliases("mu")
                .withSubcommand(new CommandReload().get())
                .withSubcommand(new CommandVersion().get())
                .withSubcommand(new CommandPrefix().get())
                .withSubcommand(new CommandSuffix().get())
                .withSubcommand(new CommandWeather().get())
                .withSubcommand(new CommandTime().get())
                .withSubcommand(new CommandToggle().get())
                .withSubcommand(new CommandTell().get());

        // add every optional commands
        new CommandFireball().get().forEach(mu::withSubcommand);

        mu.register();
    }

    public void unregister() {
        CommandAPI.unregister(COMMAND_NAME);
    }

}
