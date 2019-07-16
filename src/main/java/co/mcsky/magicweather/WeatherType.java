package co.mcsky.magicweather;

import co.mcsky.MoeUtils;
import co.mcsky.utils.MoeLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum WeatherType {
    RAIN("storm"),
    CLEAR("sun"),
    THUNDER("thunder");

    private final String commandName;

    WeatherType(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getName(MoeUtils moe) {
        switch (this) {
            case CLEAR:
                return moe.config.magicweather_message_clear;
            case RAIN:
                return moe.config.magicweather_message_rain;
            case THUNDER:
                return moe.config.magicweather_message_thunder;
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
        int duration = 3600;
        switch (this) {
            case CLEAR:
            case RAIN:
                moe.getServer().dispatchCommand(
                        console, String.format("essentials:weather %s %s %d", world, getCommandName(), duration)
                );
                break;
            case THUNDER:
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.getWorld().setThunderDuration(MoeLib.toTick(duration));
                break;
            default:
                throw new IllegalStateException("Unknown weather value.");
        }
    }
}
