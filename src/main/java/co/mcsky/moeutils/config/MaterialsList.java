package co.mcsky.moeutils.config;

import co.mcsky.moeutils.MoeUtils;
import org.bukkit.Material;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public final class MaterialsList {

    public MaterialsList() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                                                                      .path(new File(MoeUtils.plugin.getDataFolder(), "materials.yml").toPath())
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
            root.node("materials").act(node -> {
                for (Material value : Material.values()) {
                    node.appendListNode().set(Material.class, value);
                }
            });
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            MoeUtils.plugin.getLogger().warning("Unable to save materials.yml!");
        }

    }

}
