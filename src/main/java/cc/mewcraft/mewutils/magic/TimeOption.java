package cc.mewcraft.mewutils.magic;

import cc.mewcraft.mewutils.MewUtils;
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
        return switch (this) {
            case DAY -> MewUtils.text("magic-time.day");
            case NIGHT -> MewUtils.text("magic-time.night");
        };
    }

    public void set(World world) {
        world.setTime(time);
    }

}
