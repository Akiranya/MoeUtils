package co.mcsky.magictime;

import co.mcsky.MoeUtils;
import org.bukkit.command.CommandSender;

public enum TimeType {
    DAY("day"),
    NIGHT("night");

    private final String commandName;

    TimeType(String commandName) {
        this.commandName = commandName;
    }

    public String getName(MoeUtils pl) {
        switch (this) {
            case DAY:
                return pl.getMoeConfig().MAGICTIME_MESSAGE_DAY;
            case NIGHT:
                return pl.getMoeConfig().MAGICTIME_MESSAGE_NIGHT;
            default:
                throw new IllegalStateException("Unknown time value.");
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public void setTime(MoeUtils pl) {
        CommandSender console = pl.getServer().getConsoleSender();
        String command = String.format("essentials:time %s all", getCommandName());
        pl.getServer().dispatchCommand(console, command);
    }
}
