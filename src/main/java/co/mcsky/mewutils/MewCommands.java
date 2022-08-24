package co.mcsky.mewutils;

import co.mcsky.mewutils.commands.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

public class MewCommands {

    public static final String COMMAND_NAME = "mewutils";
    public static boolean firstLoad = false;

    public void register() {
        firstLoad = true;
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
                .withSubcommand(new CommandSlimeChunk().get())
                .register();
    }

    public void unregister() {
        CommandAPI.unregister(COMMAND_NAME);
    }

}
