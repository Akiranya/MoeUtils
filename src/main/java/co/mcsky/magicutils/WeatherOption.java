package co.mcsky.magicutils;

import co.mcsky.LanguageRepository;
import co.mcsky.utilities.TimeConverter;
import org.bukkit.World;

public enum WeatherOption {
    RAIN,
    CLEAR,
    THUNDER;

    /**
     * Get the fancy name!
     *
     * @param lang Messages instance of MoeUtils.
     *
     * @return The fancy name of this {@code weather}.
     */
    public String customName(LanguageRepository lang) {
        switch (this) {
            case CLEAR:
                return lang.magicweather_clear;
            case RAIN:
                return lang.magicweather_rain;
            case THUNDER:
                return lang.magicweather_thunder;
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
