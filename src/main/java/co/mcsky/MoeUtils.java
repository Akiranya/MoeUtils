package co.mcsky;

import co.mcsky.betterbees.BeehiveBeeCounter;
import co.mcsky.config.*;
import co.mcsky.foundiamonds.PlayerListener;
import co.mcsky.misc.CreatureDeathLogger;
import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.safeportal.PlayerTeleportListener;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MoeUtils extends JavaPlugin {
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

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
//        try {
//            config.save();
//        } catch(InvalidConfigurationException ex) {
//            ex.printStackTrace();
//        }
//        this.saveConfig(); // Save setting to disk.
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        loadConfig();
//        this.saveDefaultConfig();
        if (!setupVault()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new CommandHandler(this);
        new ArenaEventListener(this);
        new PlayerTeleportListener(this);
        new PlayerListener(this);
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

    private void loadConfig() {
        try {
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
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

}
