package co.mcsky.moeutils.i18n;

import co.mcsky.moeutils.MoeUtils;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Since there is no method like {@code getBlockDisplayName} in {@link
 * LanguageHelper}, I have created a wrapper class for quickly getting the
 * {@code i18nDisplayName} of a given block.
 */
public final class I18nBlock {

    private static final Map<Material, String> displayNames = new HashMap<>();

    public static String getBlockDisplayName(Material material) {
        if (!displayNames.containsKey(material)) {
            registerEntry(material);
        }
        return displayNames.get(material);
    }

    private static void registerEntry(Material material) {
        String default_lang = MoeUtils.plugin.config.LANG;
        displayNames.put(material, LanguageHelper.getItemDisplayName(new ItemStack(material), default_lang));
    }

}
