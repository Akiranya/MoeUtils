package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.World;

public enum WeatherOption {
    RAIN,
    CLEAR,
    THUNDER;

    /**
     * Get the fancy name!
     *
     * @param moe MoeUtils instance.
     *
     * @return The fancy name of this {@code weather}.
     */
    public String customName(MoeUtils moe) {
        switch (this) {
            case CLEAR:
                return moe.magicWeatherConfig.msg_clear;
            case RAIN:
                return moe.magicWeatherConfig.msg_rain;
            case THUNDER:
                return moe.magicWeatherConfig.msg_thunder;
            default:
                throw new IllegalStateException("Unknown weather type.");
        }
    }

    public void set(World world) {
        int duration = TimeConverter.toTick(3600);
        switch (this) {
            case CLEAR:
                world.setStorm(false);
                break;
            case RAIN:
                world.setStorm(true);
                world.setWeatherDuration(duration);
                break;
            case THUNDER:
                world.setStorm(true);
                world.setWeatherDuration(duration);
                world.setThundering(true);
                world.setThunderDuration(duration);
                break;
            default:
                throw new IllegalStateException("Unknown weather type.");
        }
    }
}
