package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moecore.text.Text;
import co.mcsky.moecore.text.TextRepository;
import co.mcsky.moeutils.chat.CustomPrefix;
import co.mcsky.moeutils.chat.CustomSuffix;
import co.mcsky.moeutils.data.Datasource;
import co.mcsky.moeutils.data.DatasourceFileHandler;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.misc.*;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

public class MoeUtils extends ExtendedJavaPlugin {

    public static MoeUtils plugin;

    private MoeConfig config;
    private Economy economy;
    private LanguageManager languageManager;
    private TextRepository textRepository;
    private MagicTime magicTime;
    private MagicWeather magicWeather;
    private FoundOres foundOres;
    private Datasource datasource;
    private DatasourceFileHandler datasourceFileHandler;

    /**
     * @param module the module name
     * @param status whether the module is set to be enabled or disabled
     * @return true, if the module is set to be disabled, otherwise false
     */
    public static boolean logActiveStatus(String module, boolean status) {
        if (status) {
            plugin.getLogger().info(module + " is enabled");
        } else {
            plugin.getLogger().info(module + " is disabled");
        }
        return !status;
    }

    public static Logger logger() {
        return plugin.getLogger();
    }

    public static MoeConfig config() {
        return plugin.config;
    }

    public static Economy economy() {
        return plugin.economy;
    }

    public static Datasource datasource() {
        return plugin.datasource;
    }

    public static String text(String key, Object... replacements) {
        if (replacements.length == 0) {
            return plugin.languageManager.getDefaultConfig().get(key);
        } else {
            String[] list = new String[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                if (replacements[i] instanceof Double || replacements[i] instanceof Float) {
                    list[i] = list[i] = BigDecimal.valueOf(((Number) replacements[i]).doubleValue()).setScale(3, RoundingMode.HALF_UP).toPlainString();
                } else {
                    list[i] = replacements[i].toString();
                }
            }
            return plugin.languageManager.getDefaultConfig().get(key, list);
        }
    }

    public static Text text3(String key) {
        return plugin.textRepository.get(key);
    }

    @Override
    protected void load() {

    }

    @Override
    protected void enable() {
        plugin = this;

        // hook into Vault
        try {
            economy = Services.load(Economy.class);
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
            getLogger().severe("Some vault registration is not present");
            disable();
            return;
        }

        // initialize language manager
        initializeLanguageManager();

        // initialize config manager
        config = new MoeConfig();
        config.load();
        config.save(); // save config nodes into file

        // initialize data source
        datasourceFileHandler = new DatasourceFileHandler(getDataFolder());
        datasource = datasourceFileHandler.load().orElse(new Datasource());
        datasourceFileHandler.save(datasource);

        // initialize functions & initialize config nodes
        // in this stage, config node is initialized (with default values if nothing is present in the file)
        initializeModules();

        // register commands
        registerCommands();
    }

    @Override
    protected void disable() {
        datasourceFileHandler.save(datasource);
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    private void initializeLanguageManager() {
        languageManager = new LanguageManager(this, "languages", "zh");
        languageManager.setPlaceholderPrefix("{");
        languageManager.setPlaceholderSuffix("}");
        languageManager.setProvider(sender -> {
            if (sender instanceof Player)
                return ((Player) sender).locale().getLanguage();
            return null;
        });
        textRepository = new TextRepository(MoeUtils::text);
    }

    private void initializeModules() {
        bindModule(new BetterPortals());
        bindModule(new DeathLogger());
        bindModule(new BetterBees());
        bindModule(new LoginGuard());
        bindModule(new EndEyeChanger());
        foundOres = bindModule(new FoundOres());
        magicTime = bindModule(new MagicTime());
        magicWeather = bindModule(new MagicWeather());
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerDependency(MagicTime.class, magicTime);
        manager.registerDependency(MagicWeather.class, magicWeather);
        manager.registerDependency(Datasource.class, datasource);
        manager.registerDependency(FoundOres.class, foundOres);
        manager.registerDependency(CustomPrefix.class, new CustomPrefix());
        manager.registerDependency(CustomSuffix.class, new CustomSuffix());
        manager.registerCommand(new MoeCommands());
    }

}
