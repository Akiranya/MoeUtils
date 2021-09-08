package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.command.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

public record MoeCommands(PaperCommandManager manager) {

    public MoeCommands(PaperCommandManager manager) {
        this.manager = manager;

        // must first register conditions & completions
        registerCompletions();
        registerConditions();

        // then register each command
        manager.registerCommand(new AdminCommand());
        manager.registerCommand(new ChatMetaCommand());
        manager.registerCommand(new MagicUtilCommand());
        manager.registerCommand(new MiniMessageCommand());
        manager.registerCommand(new MiscCommand());
    }

    private void registerCompletions() {
        manager.getCommandCompletions().registerCompletion("worlds", c -> Bukkit.getWorlds().stream().map(World::getName).toList());
    }

    private void registerConditions() {

    }

}
