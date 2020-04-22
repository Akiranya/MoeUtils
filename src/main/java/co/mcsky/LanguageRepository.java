package co.mcsky;

import co.mcsky.config.converter.StringConverter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.LinkedHashMap;

public class LanguageRepository extends YamlConfig {

    @Path("acf-core")
    private LinkedHashMap<String, String> acf_core = new LinkedHashMap<>() {{
        put("permission_denied", "抱歉, 你没有权限使用这个指令.");
        put("permission_denied_parameter", "抱歉, 你没有权限使用这个指令 (参数限制).");
        put("error_generic_logged", "发生了一个错误. 问题已记录在. 请将此问题汇报给服务器管理员.");
        put("unknown_command", "没有该指令, 输入 <c2>/help</c2> 查看帮助");
        put("invalid_syntax", "用法: <c2>{command}</c2> <c3>{syntax}</c3>");
        put("error_prefix", "错误: {message}");
        put("error_performing_command", "抱歉, 执行该指令时服务器内部发生了一个错误.");
        put("info_message", "{message}");
        put("please_specify_one_of", "错误: 请从 (<c2>{valid}</c2>) 选择一个值.");
        put("must_be_a_number", "错误: {num} 必须时一个数字.");
        put("must_be_min_length", "错误: 最小为 {min} 个字符长.");
        put("must_be_max_length", "错误: 最多为 {max} 个字符长.");
        put("please_specify_at_most", "错误: 最多只能设置为 {max}.");
        put("please_specify_at_least", "错误: 请指定一个比 {min} 大的值.");
        put("not_allowed_on_console", "错误: 控制台无法使用该指令.");
        put("could_not_find_player", "错误: 无法找到玩家: <c2>{search}</c2>");
        put("no_command_matched_search", "没有帮助信息包含 <c2>{search}</c2>.");
        put("help_page_information", "- 显示页面 <c2>{page}</c2> / <c2>{totalpages}</c2> (共 <c3>{results}</c3> 条指令).");
        put("help_no_results", "错误: 没有更多页面了.");
        put("help_header", "<c3>=== </c3><c1>帮助信息之指令 </c1><c2>{commandprefix}{command}</c2><c3> ===</c3>");
        put("help_format", "<c1>{command}</c1> <c2>{parameters}</c2> <c3>{separator} {description}</c3>");
        put("help_detailed_header", "<c3>=== </c3><c1>详细帮助信息之 </c1><c2>{commandprefix}{command}</c2><c3> ===</c3>");
        put("help_detailed_command_format", "<c1>{command}</c1> <c2>{parameters}</c2> <c3>{separator} {description}</c3>");
        put("help_detailed_parameter_format", "<c2>{syntaxorname}</c2>: <c3>{description}</c3>");
        put("help_search_header", "<c3>=== </c3><c1>搜索结果之 </c1><c2>{commandprefix}{command} {search}</c2><c3> ===</c3>");
    }};
    @Path("acf-minecraft")
    private LinkedHashMap<String, String> acf_minecraft = new LinkedHashMap<>() {{
        put("player_is_vanished_confirm", "警告: <c2>{vanished}</c2> 已隐身. 请不要惊动他!\n" +
                                          "要确认你的操作, 在他的名字后面加上 <c2>:confirm</c2>.\n" +
                                          "例子: <c2>{vanished}:confirm</c2>");
        put("invalid_world", "错误: 世界不存在.");
        put("you_must_be_holding_item", "错误: 你的手上必须拿着一个物品.");
        put("username_too_short", "错误: 用户名太短, 必须为三个字符长.");
        put("is_not_a_valid_name", "错误: <c2>{name}</c2> 不是一个有效的用户名.");
        put("multiple_players_match", "错误: 有多名玩家的名字对上了 <c2>{search}</c2> <c3>({all})</c3>, 请指定更佳完整的用户名.");
        put("no_player_found_server", "玩家 <c2>{search}</c2> 好像还没有来过此服务器.");
        put("no_player_found_offline", "未找到叫做 <c2>{search}</c2> 的玩家.");
        put("no_player_found", "未找到叫做 <c2>{search}</c2> 的玩家.");
        put("location_please_specify_world", "错误: 请指定一个世界. 例子: <c2>world:x,y,z</c2>.");
        put("location_please_specify_xyz", "错误: 请指定坐标的 x, y, z. 例子: <c2>world:x,y,z</c2>.");
        put("location_console_not_relative", "错误: 控制台无法使用相对坐标.");
    }};
    @Path("common.lang")
    public String common_lang = "zh_cn";
    @Path("common.none")
    public String common_none = "无";
    @Path("common.enabled")
    public String common_enabled = "是";
    @Path("common.disabled")
    public String common_disabled = "否";
    @Path("common.no_perms")
    public String common_noperms = "&c你没有权限使用这个指令: %s";
    @Path("common.only_players")
    public String common_only_players = "只有玩家才可以使用这个指令.";
    @Path("common.not_enough_money")
    public String common_not_enough_money = "&7你没有足够的金钱.";
    @Path("common.reloaded")
    public String common_reloaded = "&bMoeUtils 已重新载入 (耗时 %s ms).";
    @Path("common.version")
    public String common_version = "&b正在使用 MoeUtils %s.";
    @Path("common.price")
    public String common_charge = "&f你花掉了 %d &f金钱.";
    @Path("common.cooldown")
    public String common_cooldown = "&7冷却中:&b %d&7 秒.";
    @Path("common.reset")
    public String common_reset = "&b状态已重置.";
    @Path("betterbees.count_beehive")
    public String betterbees_count_beehive = "这个蜂箱里有 %s 只蜜蜂.";
    @Path("betterbees.count_bee_nest")
    public String betterbees_count_bee_nest = "这个蜂巢里有 %s 只蜜蜂.";
    @Path("betterbees.reminder_on_place")
    public String betterbees_reminder_on_place = "&7提示: &6右击蜂箱/蜂巢&7 可查看内部蜜蜂数量.";
    @Path("betterbees.reminder_in_hand")
    public String betterbees_reminder_in_hand = "&7提示: &6手持蜂箱 Shift+右键空气&7 可查看内部蜜蜂数量.";
    @Path("deathlogger.death")
    public String deathlogger_entity_death = "&8刚刚有一只&7%s&8从人间蒸发了 (原因: &7%s&8) (附近玩家: &7%s&8) (坐标: &7%s&8)";
    @Path("foundores.prefix")
    public String foundores_prefix = "&8[&e矿工茶馆&8]&r ";
    @Path("foundores.found")
    public String foundores_found = "%s&7 挖到了 &e%d&7 个 &r&l%s!";
    @Path("magictime.prefix")
    public String magictime_prefix = "&8[&d魔法时间&8]&r ";
    @Path("magictime.day")
    public String magictime_day = "魔法白天";
    @Path("magictime.night")
    public String magictime_night = "魔法夜晚";
    @Path("magictime.changed")
    public String magictime_changed = "&b一位神秘的魔法师把所有世界的时间变成了 &d%s&b.";
    @Path("magictime.ended")
    public String magictime_ended = "&d%s&b 结束了! &7(冷却就绪)";
    @Path("magicweather.prefix")
    public String magicweather_prefix = "&8[&d魔法天气&8]&r ";
    @Path("magicweather.clear")
    public String magicweather_clear = "魔法晴天";
    @Path("magicweather.rain")
    public String magicweather_rain = "魔法阴天";
    @Path("magicweather.thunder")
    public String magicweather_thunder = "魔法雷雨";
    @Path("magicweather.changed")
    public String magicweather_changed = "&b一位神秘法师把世界 &e%s&b 的天气变成了 &d%s&b.";
    @Path("magicweather.ended")
    public String magicweather_ended = "&b神秘法师的 &e%s&b (世界 &e%s&b) 结束了! &7(冷却就绪)";
    @Path("betterportals.cancelled")
    public String betterportals_cancelled = "&e监测到该地狱门的目的地在世界边界外,为了您的安全本次传送已取消.";
    @Path("betterportals.debug")
    public String betterportals_debug = "PlayerPortalEvent has been cancelled for %s.";

    public LanguageRepository(Plugin plugin) {
        try {
            CONFIG_FILE = new File(plugin.getDataFolder(), "lang/lang_zh.yml");
            CONFIG_HEADER = new String[]{"Language file of MoeUtils."};
            addConverter(StringConverter.class);
            init();
        } catch (InvalidConfigurationException | InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}

