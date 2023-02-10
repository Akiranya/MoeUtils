package cc.mewcraft.mewutils.magic;

import cc.mewcraft.mewutils.MewUtils;
import org.bukkit.World;

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
        return switch (this) {
            case CLEAR -> MewUtils.translations().of("magic_weather.clear").plain();
            case RAIN -> MewUtils.translations().of("magic_weather.rain").plain();
            case THUNDER -> MewUtils.translations().of("magic_weather.thunder").plain();
        };
    }

    public void set(World world) {
        int duration = 3600 * 20;
        switch (this) {
            case CLEAR -> world.setStorm(false);
            case RAIN -> {
                world.setStorm(true);
                world.setWeatherDuration(duration);
            }
            case THUNDER -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(duration);
                world.setThunderDuration(duration);
            }
            default -> throw new IllegalStateException("Unknown weather type.");
        }
    }
}
