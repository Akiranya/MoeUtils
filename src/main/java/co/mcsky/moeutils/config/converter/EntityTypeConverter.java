package co.mcsky.moeutils.config.converter;

import net.cubespace.Yamler.Config.Converter.Converter;
import net.cubespace.Yamler.Config.InternalConverter;
import org.bukkit.entity.EntityType;

import java.lang.reflect.ParameterizedType;

public class EntityTypeConverter implements Converter {

    public EntityTypeConverter(InternalConverter converter) {
    }

    @Override
    public Object toConfig(Class<?> type, Object obj,
                           ParameterizedType parameterizedType) throws
                                                                Exception {
        return obj.toString().toLowerCase();
    }

    @Override
    public Object fromConfig(Class<?> type, Object obj,
                             ParameterizedType parameterizedType) throws
                                                                  Exception {
        return EntityType.valueOf(obj.toString().toUpperCase());
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.isAssignableFrom(EntityType.class);
    }

}
