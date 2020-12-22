package co.mcsky.moeutils.magicutils;

import org.bukkit.World;

import static co.mcsky.moeutils.MoeUtils.plugin;

public enum TimeOption {
    DAY(0),
    NIGHT(14000);

    private final long time;

    TimeOption(long time) {
        this.time = time;
    }

    /**
     * Get the fancy name!
     *
     * @return The fancy name of this {@code time}.
     */
    public String customName() {
        switch (this) {
            case DAY:
                return plugin.getMessage(null, "magictime.day");
            case NIGHT:
                return plugin.getMessage(null, "magictime.night");
            default:
                throw new IllegalStateException("Unknown time type.");
        }
    }

    public void set(World world) {
        world.setTime(time);
    }

}
