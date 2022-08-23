package co.mcsky.mewutils;

import co.mcsky.mewcore.text.Text;
import co.mcsky.mewcore.text.TextRepository;
import co.mcsky.mewutils.external.MewPlaceholderExpansion;
import co.mcsky.mewutils.foundores.FoundOres;
import co.mcsky.mewutils.magic.MagicTime;
import co.mcsky.mewutils.magic.MagicWeather;
import co.mcsky.mewutils.misc.*;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.utils.Log;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MewUtils extends ExtendedJavaPlugin {

    /* plugin inst */
    public static MewUtils p;

    /* configuration */
    private MewConfig config;

    /* language loader */
    private LanguageManager languageManager;
    private TextRepository textRepository;

    /* eco & perm & chat */
    private Economy economy;

    /* commands */
    private MewCommands commands;

    /* modules */
    private MagicTime magicTime;
    private MagicWeather magicWeather;
    private BetterPortals betterPortals;
    private BetterBees betterBees;
    private DeathLogger deathLogger;
    private SlowElytra slowElytra;
    private FoundOres foundOres;
    private MergeLimit mergeLimit;

    public static void debug(String message) {
        if (MewUtils.config().debug)
            Log.info("[DEBUG] " + message);
    }

    public static void debug(Throwable message) {
        if (MewUtils.config().debug)
            Log.info("[DEBUG] " + message.getMessage());
    }

    public static boolean logStatus(String module, boolean status) {
        if (status) {
            Log.info(module + " is enabled");
        } else {
            Log.info(module + " is disabled");
        }
        return !status;
    }

    public static MewConfig config() {
        return p.config;
    }

    public static Economy economy() {
        return p.economy;
    }

    public static String text(String key, Object... replacements) {
        String message;
        if (replacements.length == 0) {
            message = p.languageManager.getDefaultConfig().get(key);
        } else {
            String[] list = new String[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                list[i] = replacements[i].toString();
            }
            message = p.languageManager.getDefaultConfig().get(key, list);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Text text3(String key) {
        return p.textRepository.get(key);
    }

    public MagicTime getMagicTime() {
        return magicTime;
    }

    public MagicWeather getMagicWeather() {
        return magicWeather;
    }

    public BetterPortals getBetterPortals() {
        return betterPortals;
    }

    public BetterBees getBetterBees() {
        return betterBees;
    }

    public DeathLogger getDeathLogger() {
        return deathLogger;
    }

    public SlowElytra getSlowElytra() {
        return slowElytra;
    }

    public FoundOres getFoundOres() {
        return foundOres;
    }

    @Override
    protected void load() {

    }

    @Override
    protected void enable() {
        p = this;

        // hook into Vault
        try {
            economy = Services.load(Economy.class);
        } catch (Exception e) {
            Log.severe(e.getMessage());
            Log.severe("Some vault registration is not present");
            disable();
            return;
        }

        // initialize language manager
        initializeLanguageManager();

        // initialize config manager
        config = new MewConfig();
        config.load();
        config.save(); // save config nodes into file

        initializeModules();
        commands = new MewCommands();
        commands.register();
    }

    @Override
    protected void disable() {
        commands.unregister();
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
        textRepository = new TextRepository(MewUtils::text);
    }

    private void initializeModules() {
        magicTime = bindModule(new MagicTime());
        magicWeather = bindModule(new MagicWeather());
        betterPortals = bindModule(new BetterPortals());
        deathLogger = bindModule(new DeathLogger());
        betterBees = bindModule(new BetterBees());
        slowElytra = bindModule(new SlowElytra());
        foundOres = bindModule(new FoundOres());
        mergeLimit = bindModule(new MergeLimit());
    }

    private void hookExternal() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MewPlaceholderExpansion().register();
            Log.info("Hooked into PlaceholderAPI");
        }
    }

}