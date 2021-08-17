package co.mcsky.moeutils.data;

import co.mcsky.moecore.config.YamlConfigFactory;
import me.lucko.helper.serialize.FileStorageHandler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class DatasourceFileHandler extends FileStorageHandler<Datasource> {

    private final static String fileName = "data";
    private final static String fileExt = ".yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public DatasourceFileHandler(File dataFolder) {
        super(fileName, fileExt, dataFolder);
        TypeSerializerCollection serializers = YamlConfigFactory.typeSerializers().childBuilder()
                .register(Datasource.class, DataSourceSerializer.INSTANCE)
                .build();
        loader = YamlConfigurationLoader.builder()
                .file(new File(dataFolder, fileName + fileExt))
                .defaultOptions(opt -> opt.serializers(serializers))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
    }

    @Override
    protected Datasource readFromFile(Path path) {
        try {
            return (root = loader.load()).get(Datasource.class);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void saveToFile(Path path, Datasource dataSource) {
        try {
            loader.save(root.set(dataSource));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

}
