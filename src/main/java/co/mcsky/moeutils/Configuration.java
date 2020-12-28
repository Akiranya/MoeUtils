package co.mcsky.moeutils;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

import static co.mcsky.moeutils.MoeUtils.plugin;

public final class Configuration {

    public static final String configFileName = "config.yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public Configuration() {
        // Initialize the loader for config file config.yml
        loader = YamlConfigurationLoader.builder()
                                        .indent(4)
                                        .path(new File(plugin.getDataFolder(), configFileName).toPath())
                                        .nodeStyle(NodeStyle.BLOCK)
                                        .build();
        // Load the config file contents into memory
        load();

        // Output important configs
//        log();

        // Save config file
//        save();
    }

    public CommentedConfigurationNode node(Object... path) {
        if (root == null) {
            plugin.getLogger().severe("Config is not loaded yet!");
            throw new IllegalStateException();
        }
        return root.node(path);
    }

    public void load() {
        // Load config from file, assigning the config contents to root
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Failed to load" + configFileName + ": " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Unable to save " + configFileName + ": " + e.getMessage());
        }
    }

    public String getLanguage() {
        return node("lang").getString("zh_cn");
    }

}
