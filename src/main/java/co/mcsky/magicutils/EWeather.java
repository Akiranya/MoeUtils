package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static co.mcsky.util.MoeLib.toTick;

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
                player.getWorld().setThunderDuration(toTick(duration));
                break;
            default:
                throw new IllegalStateException("Unknown weather value.");
        }
    }
}
