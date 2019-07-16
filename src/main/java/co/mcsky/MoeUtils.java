package co.mcsky;

import co.mcsky.magictime.MagicTime;
import co.mcsky.magicweather.MagicWeather;
import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.safeportal.PlayerTeleportListener;
import com.garbagemule.MobArena.MobArena;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MoeUtils extends JavaPlugin {
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    public MoeConfig config;
    private MobArena mobarena;

    @Override
    public void onDisable() {
        // Save config to disk. It will destroy any data in memory.
        this.saveConfig();

        HandlerList.unregisterAll(this);

        /* Cancel 'postponed broadcast' task */
        MagicTime.getInstance(this).cancelBroadcastTask();
        MagicWeather.getInstance(this).cancelBroadcastTask();
    }

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        config = MoeConfig.getInstance(this);

        // Set up Vault
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        // Register commands
        new Commands(this);

        // Set up MobArena-Addon
        setupMobArena();
        if (mobarena != null) {
            new ArenaEventListener(this, mobarena, new CustomPlayerName());
        }

        // Set up Safe-Portal
        new PlayerTeleportListener(this);
    }

    public MoeConfig getMoeConfig() {
        return config;
    }

    private void setupMobArena() {
        Plugin plugin = getServer().getPluginManager().getPlugin("MobArena");
        this.mobarena = plugin != null ? (MobArena) plugin : null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return permission != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return chat != null;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }

}
