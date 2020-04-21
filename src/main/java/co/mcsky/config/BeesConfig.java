package co.mcsky.config;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BeesConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comment("Whether requires player to sneak for counting or not")
    public boolean requireSneak = false;

    public BeesConfig(Plugin plugin) {
        try {
            CONFIG_HEADER = new String[]{"Configuration of the BetterBees"};
            CONFIG_FILE = new File(plugin.getDataFolder(), "betterbees.yml");
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
