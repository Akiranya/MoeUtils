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
import java.util.UUID;

public class DataSourceSerializer implements TypeSerializer<Datasource> {

    public static final DataSourceSerializer INSTANCE = new DataSourceSerializer();

    @Override
    public Datasource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final Datasource datasource = new Datasource();
        final List<Location> locations = node.node("end-eye-target-locations").getList(Location.class, new ArrayList<>());
        final List<UUID> listeners = node.node("found-ores-listeners").getList(UUID.class, new ArrayList<>());
        datasource.getEndPortalsData().addEndEyeTargetLocation(locations);
        return datasource;
    }

    @Override
    public void serialize(Type type, @Nullable Datasource obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("end-eye-target-locations").setList(Location.class, obj.getEndPortalsData().getEndEyeTargetLocations());
    }
}
