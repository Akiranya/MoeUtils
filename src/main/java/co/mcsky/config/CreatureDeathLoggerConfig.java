package co.mcsky.config;

import co.mcsky.config.converter.EntityTypeConverter;
import co.mcsky.config.converter.StringConverter;
import net.cubespace.Yamler.Config.*;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;

public class CreatureDeathLoggerConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comments({"The radius of searching for nearby players around the death entity.",
               "The search only happens if there is no direct killer to the death entity."})
    public int searchRadius = 16;
    @Comment("List of creatures that should be logged when died.")
    public java.util.Set<EntityType> creatures = new HashSet<>() {{
        add(EntityType.VILLAGER);
    }};
    @Path("messages.death")
    public String msg_death = "&8刚刚有一只&7%s&8从人间蒸发了 (原因: &7%s&8) (附近玩家: &7%s&8) (坐标: &7%s&8)";

    public CreatureDeathLoggerConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of the CreatureDeathLogger"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "death_logger.yml");
        try {
            addConverter(EntityTypeConverter.class);
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
