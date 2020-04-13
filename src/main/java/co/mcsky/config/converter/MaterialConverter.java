package co.mcsky.config.converter;

import net.cubespace.Yamler.Config.Converter.Converter;
import net.cubespace.Yamler.Config.InternalConverter;
import org.bukkit.Material;

import java.lang.reflect.ParameterizedType;

public class MaterialConverter implements Converter {

    public MaterialConverter(InternalConverter converter) {
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
        return obj.toString();
    }

    @Override
    public Object fromConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
        return Material.valueOf(obj.toString().toUpperCase());
    }

    @Override
    public boolean supports(Class<?> type) {
        return Material.class.isAssignableFrom(type);
    }
}
