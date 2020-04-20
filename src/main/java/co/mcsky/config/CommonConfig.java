package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class CommonConfig extends YamlConfig {

    @Comment("Default language of LangUtils.")
    public String lang = "zh_cn";
    @Path("messages.none")
    public String msg_none = "无";
    @Path("messages.enabled")
    public String msg_enabled = "是";
    @Path("messages.disabled")
    public String msg_disabled = "否";
    @Path("messages.no_perms")
    public String msg_noperms = "&c你没有权限使用这个指令: %s";
    @Path("messages.only_player")
    public String msg_only_player = "只有玩家才可以使用这个指令.";
    @Path("messages.not_enough_money")
    public String msg_not_enough_money = "&7你没有足够的金钱.";
    @Path("messages.reloaded")
    public String msg_reloaded = "&bMoeUtils 已重新载入 (耗时 %s ms).";
    @Path("messages.version")
    public String msg_version = "&b正在使用 MoeUtils %s.";
    @Path("messages.price")
    public String msg_price = "&f你花掉了 %d &f金钱.";
    @Path("messages.cooldown")
    public String msg_cooldown = "&7冷却中:&b %d&7 秒.";
    @Path("messages.reset")
    public String msg_reset = "&b状态已重置.";

    public CommonConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of Common Messages"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "common_messages.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
