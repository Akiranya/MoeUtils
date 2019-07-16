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

    public String getName(MoeUtils pl) {
        switch (this) {
            case CLEAR:
                return pl.getMoeConfig().magicweather_message_clear;
            case RAIN:
                return pl.getMoeConfig().magicweather_message_rain;
            case THUNDER:
                return pl.getMoeConfig().magicweather_message_thunder;
            default:
                throw new IllegalStateException("Unknown weather value.");
        }
    }

    /**
     * Depending on the enum value, changes the weather for world where player runs the command.
     *
     * @param pl     MoeUtils.
     * @param player Who changes the weather.
     */
    public void setWeather(MoeUtils pl, Player player) {
        CommandSender console = pl.getServer().getConsoleSender();
        String world = player.getWorld().getName();
        int duration = 3600;
        switch (this) {
            case CLEAR:
            case RAIN:
                pl.getServer().dispatchCommand(
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
