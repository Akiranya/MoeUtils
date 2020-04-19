package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MagicWeatherConfig extends YamlConfig {

    public int cost = 50;
    public int cooldown = 600;
    @Path("messages.prefix")
    public String msg_prefix = "&8[&d魔法天气&8]&r ";
    @Path("messages.clear")
    public String msg_clear = "魔法晴天";
    @Path("messages.rain")
    public String msg_rain = "魔法阴天";
    @Path("messages.thunder")
    public String msg_thunder = "魔法雷雨";
    @Path("messages.changed")
    public String msg_changed = "&b一位神秘法师把世界 &e%s&b 的天气变成了 &d%s&b.";
    @Path("messages.ended")
    public String msg_ended = "&b神秘法师的 &e%s&b (世界 &e%s&b) 结束了! &7(冷却就绪)";
    @Path("messages.status")
    public String msg_status = "&a%s:&7 %s&a. &a触发玩家:&7 %s&a. &a剩余时间:&7 %d&a 秒.";

    public MagicWeatherConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of MagicWeather"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "magic_weather.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
