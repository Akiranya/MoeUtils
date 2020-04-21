package co.mcsky.config;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MagicTimeConfig extends YamlConfig {

    public int cost = 50;
    public int cooldown = 600;

    public MagicTimeConfig(Plugin plugin) {
        try {
            CONFIG_FILE = new File(plugin.getDataFolder(), "magic_time.yml");
            CONFIG_HEADER = new String[]{"Configuration of MagicTime"};
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
