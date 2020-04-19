package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
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
     * @param moe MoeUtils instance.
     *
     * @return The fancy name of this {@code time}.
     */
    public String customName(MoeUtils moe) {
        switch (this) {
            case DAY:
                return moe.magicTimeConfig.msg_day;
            case NIGHT:
                return moe.magicTimeConfig.msg_night;
            default:
                throw new IllegalStateException("Unknown time type.");
        }
    }

    public void set(World world) {
        world.setTime(time);
    }

}
