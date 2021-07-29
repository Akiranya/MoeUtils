package co.mcsky.moeutils.data;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataSourceSerializer implements TypeSerializer<DataSource> {

    public static final DataSourceSerializer INSTANCE = new DataSourceSerializer();

    @Override
    public DataSource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final DataSource dataSource = new DataSource();
        final List<Location> locations = node.node("locations").getList(Location.class, new ArrayList<>());
        dataSource.addEndEyeTargetLocation(locations);
        return dataSource;
    }

    @Override
    public void serialize(Type type, @Nullable DataSource obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("locations").setList(Location.class, obj.getEndEyeTargetLocations());
    }
}
