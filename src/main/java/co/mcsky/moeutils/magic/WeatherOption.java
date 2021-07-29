package co.mcsky.moeutils.magic;

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
        return switch (this) {
            case CLEAR -> plugin.getMessage(null, "magic-weather.clear");
            case RAIN -> plugin.getMessage(null, "magic-weather.rain");
            case THUNDER -> plugin.getMessage(null, "magic-weather.thunder");
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
