package co.mcsky.config;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BetterPortalsConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comment("Enable debug?")
    public boolean debug = true;

    public BetterPortalsConfig(Plugin plugin) {
        try {
            CONFIG_HEADER = new String[]{"Configuration of BetterPortals"};
            CONFIG_FILE = new File(plugin.getDataFolder(), "betterportals.yml");
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
