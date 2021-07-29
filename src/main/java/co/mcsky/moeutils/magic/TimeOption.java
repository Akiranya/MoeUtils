package co.mcsky.moeutils.magic;

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
        return switch (this) {
            case DAY -> plugin.getMessage(null, "magic-time.day");
            case NIGHT -> plugin.getMessage(null, "magic-time.night");
        };
    }

    public void set(World world) {
        world.setTime(time);
    }

}
