package co.mcsky.moeutils;

import co.mcsky.moeutils.config.YamlConfigFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

import static co.mcsky.moeutils.MoeUtils.plugin;

public final class MoeConfig {

    public static final String FILENAME = "config.yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public boolean debug;

    public MoeConfig() {
        loader = YamlConfigFactory.loader(new File(plugin.getDataFolder(), FILENAME));
    }

    public CommentedConfigurationNode node(Object... path) {
        if (root == null) {
            plugin.getLogger().severe("Config is not loaded yet!");
            throw new IllegalStateException();
        }
        return root.node(path);
    }

    /**
     * Load config from file, assigning the config contents to root
     */
    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Failed to load" + FILENAME + ": " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        // 需要的配置选项
        debug = root.node("debug").getBoolean(false);
    }

    /**
     * Save root node to file
     */
    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe("Unable to save " + FILENAME + ": " + e.getMessage());
        }
    }

    public String getLanguage() {
        return node("lang").getString("zh_cn");
    }

}
