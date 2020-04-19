package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BetterBeesConfig extends YamlConfig {
    @Getter
    @Comment("Enable this feature?")
    private final boolean enable = true;
    @Getter
    @Path("messages.count_beehive")
    private final String count_beehive = "这个蜂箱里有 %s 只蜜蜂.";
    @Getter
    @Path("messages.count_bee_nest")
    private final String count_bee_nest = "这个蜂巢里有 %s 只蜜蜂.";
    
    public BetterBeesConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of the BetterBees"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "betterbees.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }
}
