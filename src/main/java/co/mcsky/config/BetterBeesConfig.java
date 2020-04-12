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
    public BetterBeesConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of the BetterBees"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "betterbees.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Comment("Enable this feature?")
    private boolean enable = true;

    @Getter
    @Path("messages.count")
    private String count = "这个蜂巢里有 %s 只蜜蜂.";
}
