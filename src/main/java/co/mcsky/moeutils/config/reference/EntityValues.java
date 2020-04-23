package co.mcsky.moeutils.config.reference;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection", "CanBeFinal"})
public class EntityValues extends YamlConfig {

    private List<String> entities = new ArrayList<>();

    {
        for (EntityType e : EntityType.values()) {
            entities.add(e.name().toLowerCase());
        }
    }

    public EntityValues(Plugin plugin) {
        try {
            CONFIG_FILE = new File(plugin.getDataFolder(), "entities.yml");
            CONFIG_HEADER = new String[]{"List of available entities for your reference.",
                                         "This file will update automatically each time this plugin loads."};
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
