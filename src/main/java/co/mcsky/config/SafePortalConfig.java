package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class SafePortalConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comment("Enable debug?")
    public boolean debug = true;
    @Path("messages.cancelled")
    public String msg_cancelled = "&e监测到该地狱门的目的地在世界边界外,为了您的安全本次传送已取消.";
    @Path("messages.debug")
    public String msg_debug = "PlayerPortalEvent has been cancelled for %s.";

    public SafePortalConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of SafePortal"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "safeportal.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
