package co.mcsky.i18n;

import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Since there is no method like {@code getBlockDisplayName} in {@link LanguageHelper}, I have created a wrapper class
 * for quickly getting the {@code i18nDisplayName} of a given block.
 */
public class I18nBlock {
    private static final Map<Material, String> displayNames = new HashMap<>();

    public static String getBlockDisplayName(Material material, Player player) {
        if (!displayNames.containsKey(material)) {
            registerEntry(material, player);
        }
        return displayNames.get(material);
    }

    private static void registerEntry(Material material, Player player) {
        displayNames.put(material, LanguageHelper.getItemDisplayName(new ItemStack(material), player));
    }
}
