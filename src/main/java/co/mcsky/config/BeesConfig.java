package co.mcsky.config;

import co.mcsky.config.converter.StringConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BeesConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comment("Whether requires player to sneak for counting or not")
    public boolean requireSneak = false;
    @Path("messages.count_beehive")
    public String count_beehive = "这个蜂箱里有 %s 只蜜蜂.";
    @Path("messages.count_bee_nest")
    public String count_bee_nest = "这个蜂巢里有 %s 只蜜蜂.";
    @Path("messages.reminder_on_place")
    public String reminder_on_place = "&7提示: &6右击蜂箱/蜂巢&7 可查看内部蜜蜂数量.";
    @Path("messages.reminder_in_hand")
    public String reminder_in_hand = "&7提示: &6手持蜂箱 Shift+右键空气&7 可查看内部蜜蜂数量.";

    public BeesConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of the BetterBees"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "betterbees.yml");
        try {
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
