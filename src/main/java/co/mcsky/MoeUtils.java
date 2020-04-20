package co.mcsky;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.bees.BeeBase;
import co.mcsky.config.*;
import co.mcsky.foundiamonds.FoundDiamonds;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.MagicWeather;
import co.mcsky.misc.CreatureDeathLogger;
import co.mcsky.misc.OptimizedNetherPortal;
import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.utilities.CooldownUtil;
import co.mcsky.utilities.TimerUtil;
import com.earth2me.essentials.Essentials;
import com.meowj.langutils.LangUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MoeUtils extends JavaPlugin {

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public BeesConfig beesCfg;
    public CommonConfig commonCfg;
    public CreatureDeathLoggerConfig deathLoggerCfg;
    public FoundDiamondsConfig foundDiamondsCfg;
    public MagicTimeConfig magicTimeCfg;
    public MagicWeatherConfig magicWeatherCfg;
    public MobArenaProConfig mobArenaProCfg;
    public SafePortalConfig safePortalCfg;

    public MagicTime magicTime;
    public MagicWeather magicWeather;

    @Override
    public void onDisable() {
        CooldownUtil.resetAll();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        loadConfig();

        permission = getPermission();
        economy = getEconomy();
        chat = getChat();
        if (chat != null && permission != null && economy != null) {
            getLogger().info("Hooked into Vault.");
        }
        if (getEssentials() != null) {
            getLogger().info("Hooked into Essentials.");
        }
        if (getLangUtils() != null) {
            getLogger().info("Hooked into LangUtils.");
        }

        new ArenaEventListener(this);
        new OptimizedNetherPortal(this);
        new FoundDiamonds(this);
        new CreatureDeathLogger(this);
        new BeeBase(this);
        magicTime = new MagicTime(this);
        magicWeather = new MagicWeather(this);

        // Register commands
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CommandHandler(this));

        // Print config in console
        new ConfigPrinter(this);
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

    private void loadConfig() {
        // Load config from disk
        ConfigFactory configFactory = new ConfigFactory(this);

        // Assign config references
        beesCfg = configFactory.getBeesConfig();
        commonCfg = configFactory.getCommonConfig();
        deathLoggerCfg = configFactory.getCreatureDeathLoggerConfig();
        foundDiamondsCfg = configFactory.getFoundDiamondsConfig();
        magicTimeCfg = configFactory.getMagicTimeConfig();
        magicWeatherCfg = configFactory.getMagicWeatherConfig();
        mobArenaProCfg = configFactory.getMobArenaProConfig();
        safePortalCfg = configFactory.getSafePortalConfig();
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
