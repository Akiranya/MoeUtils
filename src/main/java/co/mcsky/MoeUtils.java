package co.mcsky;

import co.mcsky.foundores.PlayerListener;
import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.notifier.CreatureDeathMessage;
import co.mcsky.safeportal.PlayerTeleportListener;
import co.mcsky.util.TagUtil;
import com.garbagemule.MobArena.MobArena;
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
    public MoeSetting setting;
    private MobArena ma;

    @Override
    public void onDisable() {
        this.saveConfig(); // save setting to disk. It will destroy any data in memory.
        HandlerList.unregisterAll(this); // unregister all listeners
        Bukkit.getScheduler().cancelTasks(this); // not sure if it works. Just having a try.
    }

    @Override
    public void onEnable() {
        // save a copy of the default setting.yml if one is not there
        this.saveDefaultConfig();
        setting = MoeSetting.getInstance(this);

        if (!(setupEconomy() && setupPermissions() && setupChat())) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new CommandHandler(this);

        setupMobArena(); // Set up MobArena

        if (ma != null) new ArenaEventListener(this, ma, new TagUtil());

        new PlayerTeleportListener(this);
        new PlayerListener(this);
        new CreatureDeathMessage(this);
    }

    private void setupMobArena() {
        Plugin ma = getServer().getPluginManager().getPlugin("MobArena");
        this.ma = ma != null ? (MobArena) ma : null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
        return permission != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) chat = chatProvider.getProvider();
        return chat != null;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) economy = economyProvider.getProvider();
        return economy != null;
    }

}
