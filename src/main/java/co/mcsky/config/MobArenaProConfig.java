package co.mcsky.config;

import co.mcsky.config.converter.EntityTypeConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;

public class MobArenaProConfig extends YamlConfig {
    public MobArenaProConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of MobArenaPro"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "mobarena_addon.yml");
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
    @Comment("List of creatures that DO NOT collide with the arrows from players.")
    private java.util.Set<EntityType> whitelist = new HashSet<>() {{
        add(EntityType.PLAYER);
        add(EntityType.WOLF);
        add(EntityType.OCELOT);
        add(EntityType.IRON_GOLEM);
        add(EntityType.HORSE);
    }};
}
