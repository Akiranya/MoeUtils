package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class CommonConfig extends YamlConfig {
    public CommonConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of Common Messages"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "common_messages.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

    @Path("messages.none")
    public String msg_none = "无";
    @Path("messages.active")
    public String msg_active = "&e是";
    @Path("messages.deactivated")
    public String msg_deactivated = "&e否";
    @Path("messages.no_perms")
    public String msg_noperms = "&c你没有权限使用这个指令:&e %s";
    @Path("messages.only_player")
    public String msg_only_player = "只有玩家才可以使用这个指令.";
    @Path("messages.not_enough_money")
    public String msg_not_enough_money = "&7你没有足够的软妹币.";
    @Path("messages.reloaded")
    public String msg_reloaded = "&eMoeUtils 已重新载入.";
    @Path("messages.cost")
    public String msg_cost = "&f你花掉了 &e%d &f软妹币.";
    @Path("messages.cooldown")
    public String msg_cooldown = "&7冷却中:&b %d&7 秒.";
    @Path("messages.reset")
    public String msg_reset = "&b%s &e已重置.";
}
