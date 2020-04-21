package co.mcsky.config;

import co.mcsky.config.converter.EntityTypeConverter;
import net.cubespace.Yamler.Config.*;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;

public class DeathLoggerConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comments({"The radius of searching for nearby players around the death entity.",
               "The search only happens if there is no direct killer to the death entity."})
    public int searchRadius = 16;
    @Comment("List of creatures that should be logged when died.")
    public java.util.Set<EntityType> creatures = new HashSet<>() {{
        add(EntityType.VILLAGER);
    }};

    public DeathLoggerConfig(Plugin plugin) {
        try {
            CONFIG_HEADER = new String[]{"Configuration of the DeathLogger"};
            CONFIG_FILE = new File(plugin.getDataFolder(), "death_logger.yml");
            addConverter(EntityTypeConverter.class);
            init();
        } catch (InvalidConfigurationException | InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
