package co.mcsky.config;

import co.mcsky.config.converter.EntityTypeConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;

public class CreatureDeathLoggerConfig extends YamlConfig {
    public CreatureDeathLoggerConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of the BetterBeesConfig"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "death_logger.yml");
        try {
            addConverter(EntityTypeConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Comment("Enable this feature?")
    private boolean enable = true;

    @Getter
    @Comment("List of creatures that should be logged when died.")
    private java.util.Set<EntityType> creatures = new HashSet<>() {{
        add(EntityType.VILLAGER);
    }};

    @Path("messages.death")
    public String msg_death = "刚刚有一只 %s 从人间蒸发了 (原因: %s) (相关玩家: %s) (坐标: %s)";

}
