package co.mcsky.moeutils.utilities;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.List;

import static co.mcsky.moeutils.MoeUtils.*;

/**
 * A convenient class to save all elements of specified enum class to file. This
 * is helpful for the user when they need to specify the name of an item or
 * entity in the config files.
 */
public class EnumValuesKeeper {

    /**
     * @param fileName  the file name (without extension)
     * @param enumClass the enum class to be examined
     */
    public static <T> void save(String fileName, Class<T> enumClass) {
        if (!enumClass.isEnum()) {
            plugin.getLogger().warning(enumClass.getName() + " is not enum class!");
            return;
        }

        fileName = fileName + ".yml";
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                                                                      .path(new File(plugin.getDataFolder(), "reference/" + fileName).toPath())
                                                                      .nodeStyle(NodeStyle.BLOCK)
                                                                      .build();
        final CommentedConfigurationNode root;
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return;
        }

        try {
            root.node("values").setList(enumClass, List.of(enumClass.getEnumConstants()));
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().warning("Unable to save " + fileName);
        }
    }

}
