package co.mcsky;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.bees.BeeBase;
import co.mcsky.commands.MagicTimeCommand;
import co.mcsky.commands.MagicWeatherCommand;
import co.mcsky.commands.MainCommand;
import co.mcsky.config.*;
import co.mcsky.config.reference.EntityValues;
import co.mcsky.config.reference.MaterialValues;
import co.mcsky.foundores.FoundOres;
import co.mcsky.misc.BetterPortals;
import co.mcsky.misc.DeathLogger;
import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimerUtil;
import com.earth2me.essentials.Essentials;
import com.meowj.langutils.LangUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class MoeUtils extends JavaPlugin {

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public PaperCommandManager manager;
    public LanguageManager languageManager;

    public BeesConfig beesCfg;
    public CreatureDeathLoggerConfig deathLoggerCfg;
    public FoundOresConfig foundDiamondsCfg;
    public MagicTimeConfig magicTimeCfg;
    public MagicWeatherConfig magicWeatherCfg;
    public MobArenaProConfig mobArenaProCfg;
    public BetterPortalsConfig safePortalCfg;

    @Override
    public void onDisable() {
        CooldownUtil.resetAll();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
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

        loadConfig();
        printConfig();
        registerCommands();

        // Initialize features
        new ArenaEventListener(this);
        new BetterPortals(this);
        new FoundOres(this);
        new DeathLogger(this);
        new BeeBase(this);
    }

    /**
     * @return The duration (in millisecond) for the plugin to reload
     */
    public long reload() {
        UUID uuid = UUID.randomUUID();
        TimerUtil.start(uuid);
        onDisable();
        onEnable();
        return TimerUtil.end(uuid);
    }

    /**
     * Load config from disk. If config file does not exist, it will create one
     * with default values embedded in this plugin. Precisely, default config
     * which are not language-related is embedded in classes. Language files are
     * embedded in the resource folder.
     */
    private void loadConfig() {
        try {
            // Language file
            languageManager = new LanguageManager(this);

            beesCfg = new BeesConfig(this);
            deathLoggerCfg = new CreatureDeathLoggerConfig(this);
            foundDiamondsCfg = new FoundOresConfig(this);
            magicTimeCfg = new MagicTimeConfig(this);
            magicWeatherCfg = new MagicWeatherConfig(this);
            mobArenaProCfg = new MobArenaProConfig(this);
            safePortalCfg = new BetterPortalsConfig(this);

            // Save lists of entities and materials for easy enum values looking up
            new MaterialValues(this).init();
            new EntityValues(this).init();
        } catch (net.cubespace.Yamler.Config.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void printConfig() {
        final String bullet = " - ";
        getLogger().info(ChatColor.YELLOW + "FoundDiamonds.blocks:");
        foundDiamondsCfg.blocks.forEach(e -> getLogger().info(bullet + e.toString().toLowerCase()));
        getLogger().info(ChatColor.YELLOW + "FoundDiamonds.worlds:");
        foundDiamondsCfg.worlds.forEach(e -> getLogger().info(bullet + e));
        getLogger().info(ChatColor.YELLOW + "MobArena-Addon.whitelist:");
        mobArenaProCfg.whitelist.forEach(e -> getLogger().info(bullet + e.toString().toLowerCase()));
    }

    private void registerCommands() {
        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.addSupportedLanguage(Locale.SIMPLIFIED_CHINESE);
        try {
            manager.getLocales().loadYamlLanguageFile("lang/lang_zh.yml", Locale.SIMPLIFIED_CHINESE);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        manager.getLocales().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        manager.registerCommand(new MainCommand(this));
        manager.registerCommand(new MagicTimeCommand(this));
        manager.registerCommand(new MagicWeatherCommand(this));
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
