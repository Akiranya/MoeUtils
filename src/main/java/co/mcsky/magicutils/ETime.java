package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import org.bukkit.command.CommandSender;

public enum ETime {
    DAY("day"),
    NIGHT("night");

    private final String commandName;

    ETime(String commandName) {
        this.commandName = commandName;
    }

    public String getName(MoeUtils moe) {
        switch (this) {
            case DAY:
                return moe.setting.magic_time.msg_day;
            case NIGHT:
                return moe.setting.magic_time.msg_night;
            default:
                throw new IllegalStateException("Unknown time value.");
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public void setTime(MoeUtils moe) {
        CommandSender console = moe.getServer().getConsoleSender();
        String command = String.format("essentials:time %s all", getCommandName());
        moe.getServer().dispatchCommand(console, command);
    }
}
