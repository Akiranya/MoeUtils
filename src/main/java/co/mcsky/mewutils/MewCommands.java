package co.mcsky.mewutils;

import co.mcsky.mewutils.commands.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

public class MewCommands {

    private static final String COMMAND_NAME = "mewutils";

    public void register() {
        new CommandAPICommand(COMMAND_NAME)
                .withAliases("mu")
                .withSubcommand(new CommandReload().get())
                .withSubcommand(new CommandVersion().get())
                .withSubcommand(new CommandWeather().get())
                .withSubcommand(new CommandTime().get())
                .withSubcommand(new CommandToggle().get())
                .withSubcommand(new CommandTell().get())
                .withSubcommands(new CommandVillager().get())
                .withSubcommands(new CommandFireball().get())
                .register();
    }

    public void unregister() {
        CommandAPI.unregister(COMMAND_NAME);
    }

}
