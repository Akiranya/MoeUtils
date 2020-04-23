package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.bees.BeeBase;
import co.mcsky.moeutils.config.Configuration;
import co.mcsky.moeutils.config.reference.EntityValues;
import co.mcsky.moeutils.config.reference.MaterialValues;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.misc.BetterPortals;
import co.mcsky.moeutils.misc.DeathLogger;
import co.mcsky.moeutils.mobarena.ArenaEventListener;
import co.mcsky.moeutils.utilities.CooldownUtil;
import co.mcsky.moeutils.utilities.TimerUtil;
import com.earth2me.essentials.Essentials;
import com.meowj.langutils.LangUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
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

    public PaperCommandManager manager;
    public Configuration config;
    public LanguageRepository lang;

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
        initConfig();
        initFunctions();
        registerCommands();
    }

    private void initFunctions() {
        new ArenaEventListener();
        new BetterPortals();
        new FoundOres();
        new DeathLogger();
        new BeeBase();
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
     * Load config from disk. If config file does not exist, it will create one
     * with default values embedded in this plugin.
     */
    private void initConfig() {
        lang = new LanguageRepository();
        config = new Configuration();
        config.print();

        // Save lists of entities and materials for easy enum values looking up
        new MaterialValues(this);
        new EntityValues(this);
    }

    private void registerCommands() {
        manager = new PaperCommandManager(this);
        //noinspection deprecation
        manager.enableUnstableAPI("help");
        manager.addSupportedLanguage(Locale.SIMPLIFIED_CHINESE);
        try {
            manager.getLocales().loadYamlLanguageFile("lang/lang_zh.yml", Locale.SIMPLIFIED_CHINESE);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        manager.getLocales().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        manager.registerDependency(MagicTime.class, new MagicTime());
        manager.registerDependency(MagicWeather.class, new MagicWeather());
        manager.getCommandReplacements().addReplacement("moe", "mu|moe|moeutils");
        manager.registerCommand(new CommandHandler());
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
