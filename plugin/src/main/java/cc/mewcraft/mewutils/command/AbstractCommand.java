package cc.mewcraft.mewutils.command;

public abstract class AbstractCommand {

    protected final CommandManager commandManager;

    public AbstractCommand(CommandManager manager) {
        this.commandManager = manager;
    }

    abstract public void register();

}

