package co.mcsky.config.reference;

import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
public class MaterialValues extends YamlConfig {

    private List<String> materials = new ArrayList<>();

    {
        for (Material m : Material.values()) {
            materials.add(m.name().toLowerCase());
        }
    }

    public MaterialValues(Plugin plugin) {
        CONFIG_HEADER = new String[]{"List of available materials for your reference.",
                                     "This file will update automatically each time this plugin loads."};
        CONFIG_FILE = new File(plugin.getDataFolder(), "list_materials.yml");
    }

}
