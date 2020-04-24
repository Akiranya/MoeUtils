package co.mcsky.moeutils.bees;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import static co.mcsky.moeutils.MoeUtils.plugin;
import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;

public class BeeBase {

    public BeeBase() {
        if (plugin.config.betterbees_enable) {
            plugin.getServer().getPluginManager().registerEvents(new BeeCounter(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BeeReminder(), plugin);
            plugin.getLogger().info("BetterBees is enabled.");
        }
    }

    boolean isSneaking(Player player) {
        return player.isSneaking() || !plugin.config.betterbees_requireSneak;
    }

    boolean isBeehive(Material type) {
        return type == BEE_NEST || type == BEEHIVE;
    }

}
