package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.MoeUtils;
import org.bukkit.World;

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
                return MoeUtils.plugin.getMessage(MoeUtils.plugin.getServer().getConsoleSender(), "magictime.day");
            case NIGHT:
                return MoeUtils.plugin.getMessage(MoeUtils.plugin.getServer().getConsoleSender(), "magictime.night");
            default:
                throw new IllegalStateException("Unknown time type.");
        }
    }

    public void set(World world) {
        world.setTime(time);
    }

}
