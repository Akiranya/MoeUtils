package co.mcsky.magicutils;

import co.mcsky.LanguageRepository;
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
     * @param lang Messages instance of MoeUtils.
     *
     * @return The fancy name of this {@code time}.
     */
    public String customName(LanguageRepository lang) {
        switch (this) {
            case DAY:
                return lang.magictime_day;
            case NIGHT:
                return lang.magictime_night;
            default:
                throw new IllegalStateException("Unknown time type.");
        }
    }

    public void set(World world) {
        world.setTime(time);
    }

}
