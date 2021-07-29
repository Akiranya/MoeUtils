package co.mcsky.moeutils.config;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;

public abstract class YamlConfigFactory {

    private static final TypeSerializerCollection TYPE_SERIALIZERS;

    static {
        TYPE_SERIALIZERS = TypeSerializerCollection.defaults().childBuilder()
                .register(Component.class, Text3TypeSerializer.INSTANCE)
                .register(Location.class, LocationSerializer.INSTANCE)
                .build();
    }

    public static YamlConfigurationLoader loader(@Nonnull Path path) {
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(path.toFile())
                .defaultOptions(opt -> opt.serializers(TYPE_SERIALIZERS))
                .build();
    }

    public static YamlConfigurationLoader loader(@Nonnull File file) {
        return loader(file.toPath());
    }

    public static TypeSerializerCollection typeSerializers() {
        return TYPE_SERIALIZERS;
    }

}
