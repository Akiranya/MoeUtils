package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.data.DataSource;
import co.mcsky.moeutils.data.DataSourceFileHandler;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.misc.*;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MoeUtils extends ExtendedJavaPlugin {

    public static MoeUtils plugin;
    public static Economy economy;

    public MoeConfig config;
    public LanguageManager lang;
    public PaperCommandManager manager;

    private MagicTime magicTime;
    private MagicWeather magicWeather;

    private DataSource dataSource;
    private DataSourceFileHandler dataSourceFileHandler;

    /**
     * Logs active status for given module.
     *
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
        dataSourceFileHandler = new DataSourceFileHandler();
        dataSource = dataSourceFileHandler.load().orElse(new DataSource());
        dataSourceFileHandler.save(dataSource);

        // initialize functions & initialize config nodes
        // in this stage, config node is initialized (with default values if nothing is present in the file)
        initializeModules();

        // register commands
        registerCommands();
    }

    @Override
    protected void disable() {
        dataSourceFileHandler.save(dataSource);
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    /**
     * Get a message from a language config for a certain sender
     *
     * @param sender       The sender to get the string for.
     * @param key          The language key in the config
     * @param replacements An option array for replacements. (2n)-th will be the
     *                     placeholder, (2n+1)-th the value. Placeholders have
     *                     to be surrounded by percentage signs: {placeholder}
     * @return The string from the config which matches the sender's language
     * (or the default one) with the replacements replaced (or an error message,
     * never null)
     */
    public String getMessage(CommandSender sender, String key, Object... replacements) {
        if (replacements.length == 0) {
            return lang.getConfig(sender).get(key);
        } else {
            return lang.getConfig(sender).get(key, Arrays.stream(replacements)
                    .map(Object::toString)
                    .toArray(String[]::new));
        }
    }

    private void initializeLanguageManager() {
        lang = new LanguageManager(this, "languages", "zh");
        lang.setPlaceholderPrefix("{");
        lang.setPlaceholderSuffix("}");
        lang.setProvider(sender -> {
            if (sender instanceof Player)
                return ((Player) sender).locale().getLanguage();
            return null;
        });
    }

    private void initializeModules() {
        // all are terminable
        bindModule(new BetterPortals());
        bindModule(new FoundOres());
        bindModule(new DeathLogger());
        bindModule(new BetterBees());
        bindModule(new LoginProtector());
        bindModule(new EndEyeChanger(dataSource));
        magicTime = bindModule(new MagicTime());
        magicWeather = bindModule(new MagicWeather());
    }

    private void registerCommands() {
        manager = new PaperCommandManager(this);
        manager.registerDependency(MagicTime.class, magicTime);
        manager.registerDependency(MagicWeather.class, magicWeather);
        manager.registerDependency(DataSource.class, dataSource);
        manager.registerCommand(new MoeCommands());
    }

}
