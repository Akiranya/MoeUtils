package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum EWeather {
    RAIN("storm"),
    CLEAR("sun"),
    THUNDER("thunder");

    private final String commandName;

    EWeather(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getName(MoeUtils moe) {
        switch (this) {
            case CLEAR:
                return moe.setting.magic_weather.msg_clear;
            case RAIN:
                return moe.setting.magic_weather.msg_rain;
            case THUNDER:
                return moe.setting.magic_weather.msg_thunder;
            default:
                throw new IllegalStateException("Unknown weather value.");
        }
    }

    /**
     * Depending on the enum value, changes the weather for world where player runs the command.
     *
     * @param moe    MoeUtils.
     * @param player Who changes the weather.
     */
    public void setWeather(MoeUtils moe, Player player) {
        CommandSender console = moe.getServer().getConsoleSender();
        String world = player.getWorld().getName();
        int duration = 3600; // In second
        switch (this) {
            case CLEAR:
            case RAIN:
                String msg = String.format("essentials:weather %s %s %d",
                        world,
                        getCommandName(),
                        duration);
                moe.getServer().dispatchCommand(console, msg);
                break;
            case THUNDER:
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.getWorld().setThunderDuration(TimeUtil.toTick(duration));
                break;
            default:
                throw new IllegalStateException("Unknown weather value.");
        }
    }
}
