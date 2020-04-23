package co.mcsky.moeutils.bees;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class BeeBase {

    public BeeBase() {
        if (plugin.config.betterbees_enable) {
            plugin.getServer().getPluginManager().registerEvents(new BeeCounter(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BeeReminder(), plugin);
            plugin.getLogger().info("BetterBees is enabled.");
        }
    }

}
