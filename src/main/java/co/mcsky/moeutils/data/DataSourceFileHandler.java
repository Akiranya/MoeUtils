package co.mcsky.moeutils.data;

import co.mcsky.moecore.config.YamlConfigFactory;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.serialize.FileStorageHandler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class DataSourceFileHandler extends FileStorageHandler<DataSource> {

    private final static String fileName = "data";
    private final static String fileExt = ".yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public DataSourceFileHandler(File dataFolder) {
        super(fileName, fileExt, dataFolder);
        TypeSerializerCollection s = YamlConfigFactory.typeSerializers().childBuilder()
                .register(DataSource.class, DataSourceSerializer.INSTANCE)
                .build();
        loader = YamlConfigFactory.loader(new File(dataFolder, fileName + fileExt));
        try {
            root = loader.load(loader.defaultOptions().serializers(s));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DataSource readFromFile(Path path) {
        try {
            return root.get(DataSource.class);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void saveToFile(Path path, DataSource dataSource) {
        try {
            loader.save(root.set(dataSource));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

}
