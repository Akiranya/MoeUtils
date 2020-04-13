package co.mcsky.config.converter;

import net.cubespace.Yamler.Config.Converter.Converter;
import net.cubespace.Yamler.Config.InternalConverter;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;

public class StringConverter implements Converter {

    public StringConverter(InternalConverter internal) {
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
        return translateAlternateColorCodes('\u00A7', (String) obj);
    }

    @Override
    public Object fromConfig(Class<?> type, Object section, ParameterizedType parameterizedType) throws Exception {
        return ChatColor.translateAlternateColorCodes('&', section.toString());
    }

    @Override
    public boolean supports(Class<?> type) {
        return type == String.class;
    }

    /**
     * This method mimics {@link ChatColor#translateAlternateColorCodes(char, java.lang.String)} except that it changes
     * the character ChatColor.COLOR_CODE back to the text containing the character {@literal &}.
     *
     * @param altColorChar    The alternate color code character to replace.
     * @param textToTranslate Text containing the alternate color code character.
     *
     * @return Text containing the {@literal &} color code character.
     */
    @NotNull
    private static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        Validate.notNull(textToTranslate, "Cannot translate null text");

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
