package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.utilities.TimeConverter;
import org.bukkit.World;

import static co.mcsky.moeutils.MoeUtils.plugin;

public enum WeatherOption {
    RAIN,
    CLEAR,
    THUNDER;

    /**
     * Get the fancy name!
     *
     * @return The fancy name of this {@code weather}.
     */
    public String customName() {
        switch (this) {
            case CLEAR:
                return plugin.getMessage(null, "magicweather.clear");
            case RAIN:
                return plugin.getMessage(null, "magicweather.rain");
            case THUNDER:
                return plugin.getMessage(null, "magicweather.thunder");
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
                world.setThundering(true);
                world.setWeatherDuration(duration);
                world.setThunderDuration(duration);
                break;
            default:
                throw new IllegalStateException("Unknown weather type.");
        }
    }
}
