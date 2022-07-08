package co.mcsky.moeutils;

import co.mcsky.moecore.text.Text;
import co.mcsky.moecore.text.TextRepository;
import co.mcsky.moeutils.chat.CustomPrefix;
import co.mcsky.moeutils.chat.CustomSuffix;
import co.mcsky.moeutils.data.Datasource;
import co.mcsky.moeutils.data.DatasourceFileHandler;
import co.mcsky.moeutils.external.MoePlaceholderExpansion;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.misc.BetterBees;
import co.mcsky.moeutils.misc.BetterPortals;
import co.mcsky.moeutils.misc.CustomEnderEye;
import co.mcsky.moeutils.misc.DeathLogger;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class MoeUtils extends ExtendedJavaPlugin {

    /* plugin inst */
    public static MoeUtils plugin;

    /* configuration */
    private MoeConfig config;

    /* language loader */
    private LanguageManager languageManager;
    private TextRepository textRepository;

    /* data files */
    private Datasource datasource;
    private DatasourceFileHandler datasourceFileHandler;

    /* eco & perm & chat */
    private Economy economy;

    /* a flag to avoid registering commands twice */
    private boolean commandsRegistered = false;

    public static boolean report(String module, boolean status) {
        if (status) {
            logger().info(module + " is enabled");
        } else {
            logger().info(module + " is disabled");
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
        String message;
        if (replacements.length == 0) {
            message = plugin.languageManager.getDefaultConfig().get(key);
        } else {
            String[] list = new String[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                list[i] = replacements[i].toString();
            }
            message = plugin.languageManager.getDefaultConfig().get(key, list);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
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

        initializeModules();
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
        bindModule(new CustomEnderEye());
        bindModule(new DeathLogger());
        bindModule(new BetterBees());
    }

    private void registerCommands() {
        if (!commandsRegistered) {
            commandsRegistered = true; // mark commands registered
            new MoeCommands(datasource, new CustomPrefix(), new CustomSuffix(), bindModule(new FoundOres()), bindModule(new MagicTime()), bindModule(new MagicWeather()));
        }
    }

    private void hookExternal() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MoePlaceholderExpansion().register();
            getLogger().info("Hooked into PlaceholderAPI");
        }
    }

}
