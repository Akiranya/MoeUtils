package co.mcsky.config;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MagicWeatherConfig extends YamlConfig {

    public int cost = 50;
    public int cooldown = 600;

    public MagicWeatherConfig(Plugin plugin) {
        try {
            CONFIG_HEADER = new String[]{"Configuration of MagicWeather"};
            CONFIG_FILE = new File(plugin.getDataFolder(), "magic_weather.yml");
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
