package co.mcsky.moeutils.config;

import co.mcsky.moeutils.MoeUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public final class EntitiesList {

    public EntitiesList() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                                                                      .path(new File(MoeUtils.plugin.getDataFolder(), "entities.yml").toPath())
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
            root.node("entities").act(node -> {
                for (EntityType value : EntityType.values()) {
                    node.appendListNode().set(EntityType.class, value);
                }
            });
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            MoeUtils.plugin.getLogger().warning("Unable to save entities.yml!");
        }
    }

}
