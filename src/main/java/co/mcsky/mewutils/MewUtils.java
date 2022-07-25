package co.mcsky.mewutils;

import co.mcsky.mewcore.text.Text;
import co.mcsky.mewcore.text.TextRepository;
import co.mcsky.mewutils.external.MewPlaceholderExpansion;
import co.mcsky.mewutils.misc.BetterBees;
import co.mcsky.mewutils.misc.BetterPortals;
import co.mcsky.mewutils.misc.DeathLogger;
import co.mcsky.mewutils.misc.SlowElytra;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

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

    public static boolean report(String module, boolean status) {
        if (status) {
            logger().info(module + " is enabled");
        } else {
            logger().info(module + " is disabled");
        }
        return !status;
    }

    public static Logger logger() {
        return p.getLogger();
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
            getLogger().severe(e.getMessage());
            getLogger().severe("Some vault registration is not present");
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
        registerCommands();
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
        bindModule(new BetterPortals());
        bindModule(new DeathLogger());
        bindModule(new BetterBees());
        bindModule(new SlowElytra());
    }

    private void registerCommands() {
        commands = new MewCommands(this);
        commands.register();
    }

    private void unregisterCommands() {
        commands.unregister();
    }

    private void hookExternal() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MewPlaceholderExpansion().register();
            getLogger().info("Hooked into PlaceholderAPI");
        }
    }

}
