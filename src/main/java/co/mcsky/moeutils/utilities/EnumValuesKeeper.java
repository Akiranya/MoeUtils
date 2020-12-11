package co.mcsky.moeutils.utilities;

import co.mcsky.moeutils.MoeUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.List;

public class EnumValuesKeeper<T> {

    public EnumValuesKeeper(String fileName, Class<T> enumClass) {
        if (!enumClass.isEnum()) {
            MoeUtils.plugin.getLogger().warning(enumClass.getName() + " is not enum class!");
            return;
        }

        fileName = fileName + ".yml";
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                                                                      .path(new File(MoeUtils.plugin.getDataFolder(), "reference/" + fileName).toPath())
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
            MoeUtils.plugin.getLogger().warning("Unable to save " + fileName);
        }
    }

}
