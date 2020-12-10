package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.bees.BetterBees;
import co.mcsky.moeutils.config.Config;
import co.mcsky.moeutils.config.EntitiesList;
import co.mcsky.moeutils.config.MaterialsList;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.misc.BetterPortals;
import co.mcsky.moeutils.misc.DeathLogger;
import co.mcsky.moeutils.utilities.CooldownUtil;
import co.mcsky.moeutils.utilities.TimerUtil;
import com.earth2me.essentials.Essentials;
import com.meowj.langutils.LangUtils;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class MoeUtils extends JavaPlugin {

    public static MoeUtils plugin;
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public Config config;
    public LanguageManager lang;
    public PaperCommandManager manager;

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        plugin = this;

        permission = getPermission();
        economy = getEconomy();
        chat = getChat();
        if (chat != null) {
            getLogger().info("Hooked into Vault Chat.");
        }
        if (permission != null) {
            getLogger().info("Hooked into Vault Permission.");
        }
        if (economy != null) {
            getLogger().info("Hooked into Vault Economy.");
        }
        if (getEssentials() != null) {
            getLogger().info("Hooked into Essentials.");
        }
        if (getLangUtils() != null) {
            getLogger().info("Hooked into LangUtils.");
        }

        initLang();
        initConfig();
        initFunctions();
        registerCommands();
    }

    /**
     * @return The duration (in millisecond) for the plugin to reload
     */
    public long reload() {
        UUID uuid = UUID.randomUUID();
        TimerUtil.start(uuid);

        CooldownUtil.resetAll();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        initConfig();
        initFunctions();

        return TimerUtil.end(uuid);
    }

    /**
     * Get a message from a language config for a certain sender
     *
     * @param sender       The sender to get the string for. (Language is based
     *                     on this)
     * @param key          The language key in the config
     * @param replacements An option array for replacements. (2n)-th will be the
     *                     placeholder, (2n+1)-th the value. Placeholders have
     *                     to be surrounded by percentage signs: %placeholder%
     *
     * @return The string from the config which matches the sender's language
     * (or the default one) with the replacements replaced (or an error message,
     * never null)
     */
    public String getMessage(CommandSender sender, String key,
                             String... replacements) {
        return lang.getConfig(sender).get(key, replacements);
    }

    private void initFunctions() {
        new BetterPortals(this);
        new FoundOres(this);
        new DeathLogger(this);
        new BetterBees(this);
    }

    private void initLang() {
        lang = new LanguageManager(this, "languages", "zh");
        lang.setProvider(sender -> {
            if (sender instanceof Player)
                return ((Player) sender).getLocale();
            return null;
        });
    }

    /**
     * Load config from disk. If config file does not exist, it will create one
     * with default values embedded in this plugin.
     */
    private void initConfig() {
        config = new Config();

        // Save lists of entities and materials for easy enum values looking up
        new MaterialsList();
        new EntitiesList();
    }

    private void registerCommands() {
        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.addSupportedLanguage(Locale.SIMPLIFIED_CHINESE);
        try {
            manager.getLocales().loadYamlLanguageFile("languages/lang.zh.yml", Locale.SIMPLIFIED_CHINESE);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        manager.getLocales().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        manager.getCommandReplacements().addReplacement("moe", "mu|moe|moeutils");
        manager.registerCommand(new CommandHandler(this, new MagicTime(this), new MagicWeather(this)));
    }

    private Permission getPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            return permissionProvider.getProvider();
        }
        return null;
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            return economyProvider.getProvider();
        }
        return null;
    }

    private Chat getChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            return chatProvider.getProvider();
        }
        return null;
    }

    private Essentials getEssentials() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Essentials");
        if (plugin != null) {
            return (Essentials) plugin;
        }
        return null;
    }

    private LangUtils getLangUtils() {
        Plugin plugin = getServer().getPluginManager().getPlugin("LangUtils");
        if (plugin != null) {
            return (LangUtils) plugin;
        }
        return null;
    }

}
