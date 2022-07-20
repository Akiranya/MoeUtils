package co.mcsky.mewutils;

import co.mcsky.mewutils.commands.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

public class MewCommands {

    private static final String COMMAND_NAME = "mewutils";
    private final MewUtils plugin;

    public MewCommands(MewUtils plugin) {
        this.plugin = plugin;
    }

    public void register() {
        new CommandAPICommand(COMMAND_NAME)
                .withAliases("mu")
                .withSubcommand(new CommandReload(plugin).get())
                .withSubcommand(new CommandVersion(plugin).get())
                .withSubcommand(new CommandPrefix(plugin).get())
                .withSubcommand(new CommandSuffix(plugin).get())
                .withSubcommand(new CommandWeather(plugin).get())
                .withSubcommand(new CommandTime(plugin).get())
                .withSubcommand(new CommandToggle(plugin).get())
                .withSubcommand(new CommandTell(plugin).get())
                .withSubcommand(new CommandFireball(plugin).get())
                .register();
    }

    public void unregister() {
        CommandAPI.unregister(COMMAND_NAME);
    }

}
