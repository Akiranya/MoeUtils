package co.mcsky;

import co.mcsky.config.*;
import co.mcsky.foundiamonds.FoundDiamonds;
import co.mcsky.misc.BeehiveBeeCounter;
import co.mcsky.misc.CreatureDeathLogger;
import co.mcsky.misc.OptimizedNetherPortal;
import co.mcsky.mobarena.ArenaEventListener;
import com.earth2me.essentials.Essentials;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MoeUtils extends JavaPlugin {
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public Essentials essentials;

    // All config goes here
    public BetterBeesConfig betterBeesConfig;
    public CommonConfig commonConfig;
    public CreatureDeathLoggerConfig creatureDeathLoggerConfig;
    public FoundDiamondsConfig foundDiamondsConfig;
    public MagicTimeConfig magicTimeConfig;
    public MagicWeatherConfig magicWeatherConfig;
    public MobArenaProConfig mobArenaProConfig;
    public SafePortalConfig safePortalConfig;

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        loadConfig();

        if (setupVault()) {
            getLogger().info("Hooked into Vault.");
        }
        if (setupEssentials()) {
            getLogger().info("Hooked into Essentials.");
        }
        if (getServer().getPluginManager().getPlugin("LangUtils") != null) {
            getLogger().info("Hooked into LangUtils.");
        }
        if (getServer().getPluginManager().getPlugin("CoreProtect") != null) {
            getLogger().info("Hooked into CoreProtect.");
        }

        new CommandHandler(this);
        new ArenaEventListener(this);
        new OptimizedNetherPortal(this);
        new FoundDiamonds(this);
        new CreatureDeathLogger(this);
        new BeehiveBeeCounter(this);
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
        if (chatProvider != null) chat = chatProvider.getProvider();
        if (economyProvider != null) economy = economyProvider.getProvider();
        return permission != null && chat != null && economy != null;
    }

    private boolean setupEssentials() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Essentials");
        if (plugin != null) {
            essentials = (Essentials) plugin;
            return true;
        }
        return false;
    }

    private void loadConfig() {
        try {
            // Load config from disk
            commonConfig = new CommonConfig(this);
            commonConfig.init();
            commonConfig.save();
            betterBeesConfig = new BetterBeesConfig(this);
            betterBeesConfig.init();
            betterBeesConfig.save();
            creatureDeathLoggerConfig = new CreatureDeathLoggerConfig(this);
            creatureDeathLoggerConfig.init();
            creatureDeathLoggerConfig.save();
            foundDiamondsConfig = new FoundDiamondsConfig(this);
            foundDiamondsConfig.init();
            foundDiamondsConfig.save();
            magicTimeConfig = new MagicTimeConfig(this);
            magicTimeConfig.init();
            magicTimeConfig.save();
            magicWeatherConfig = new MagicWeatherConfig(this);
            magicWeatherConfig.init();
            magicWeatherConfig.save();
            mobArenaProConfig = new MobArenaProConfig(this);
            mobArenaProConfig.init();
            mobArenaProConfig.save();
            safePortalConfig = new SafePortalConfig(this);
            safePortalConfig.init();
            safePortalConfig.save();

            // Print relevant config values
            new ConfigPrinter(this);
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

}
