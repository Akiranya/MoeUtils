package co.mcsky.bees;

import co.mcsky.MoeUtils;

public class BeeBase {

    public BeeBase(MoeUtils moe) {
        if (moe.beesCfg.enable) {
            moe.getServer().getPluginManager().registerEvents(new BeeCounter(moe), moe);
            moe.getServer().getPluginManager().registerEvents(new BeeReminder(moe), moe);
            moe.getLogger().info("BeehiveBeeCounter is enabled");
        }
    }

}
