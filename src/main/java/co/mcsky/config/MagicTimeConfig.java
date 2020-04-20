package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MagicTimeConfig extends YamlConfig {

    public int cost = 50;
    public int cooldown = 600;
    @Path("messages.prefix")
    public String msg_prefix = "&8[&d魔法时间&8]&r ";
    @Path("messages.day")
    public String msg_day = "魔法白天";
    @Path("messages.night")
    public String msg_night = "魔法夜晚";
    @Path("messages.changed")
    public String msg_changed = "&b一位神秘的魔法师把所有世界的时间变成了 &d%s&b.";
    @Path("messages.ended")
    public String msg_ended = "&d%s&b 结束了! &7(冷却就绪)";

    public MagicTimeConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of MagicTime"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "magic_time.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
