package co.mcsky.moeutils;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.bees.BetterBees;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magicutils.MagicTime;
import co.mcsky.moeutils.magicutils.MagicWeather;
import co.mcsky.moeutils.misc.BetterPortals;
import co.mcsky.moeutils.misc.DeathLogger;
import co.mcsky.moeutils.mobarena.MobArenaAddon;
import co.mcsky.moeutils.utilities.CooldownManager;
import co.mcsky.moeutils.utilities.EnumValuesKeeper;
import co.mcsky.moeutils.utilities.Timer;
import com.earth2me.essentials.Essentials;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class MoeUtils extends JavaPlugin {

    public static MoeUtils plugin;
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public Configuration config;
    public LanguageManager lang;
    public PaperCommandManager manager;

    private MagicTime magicTime;
    private MagicWeather magicWeather;
    private MobArenaAddon mobArenaAddon;
    private BetterPortals betterPortals;
    private FoundOres foundOres;
    private BetterBees betterBees;
    private DeathLogger deathLogger;

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Hook into Vault
        permission = getPermission();
        economy = getEconomy();
        chat = getChat();


        // Initialize language manager
        initializeLanguageManager();

        // Initialize config manager
        config = new Configuration();
        config.load();

        // Initialize functions & initialize config nodes
        // In this stage, config node is initialized (with default values if nothing is present in the file)
        initializeModules();

        // Register commands
        registerCommands();

        // Save nodes in config.yml
        config.save();

        // Save useful enum values in files
        EnumValuesKeeper.save("materials", Material.class);
        EnumValuesKeeper.save("entities", EntityType.class);
    }

    /**
     * @return The duration (in millisecond) for the plugin to reload
     */
    public long reload() {
        UUID uuid = UUID.randomUUID();
        Timer.start(uuid);

        // Reset cooldown, unregister all listeners, cancel tasks and reload config from file
        CooldownManager.resetAll();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        config.load();
        config.save();

        // Re-initialize language and functions
        initializeLanguageManager();
        initializeModules();

        return Timer.end(uuid);
    }

    /**
     * Get a message from a language config for a certain sender
     *
     * @param sender       The sender to get the string for.
     * @param key          The language key in the config
     * @param replacements An option array for replacements. (2n)-th will be the
     *                     placeholder, (2n+1)-th the value. Placeholders have
     *                     to be surrounded by percentage signs: {placeholder}
     *
     * @return The string from the config which matches the sender's language
     * (or the default one) with the replacements replaced (or an error message,
     * never null)
     */
    public String getMessage(CommandSender sender, String key, Object... replacements) {
        if (replacements.length == 0) {
            return lang.getConfig(sender).get(key);
        } else {
            return lang.getConfig(sender).get(key, Arrays.stream(replacements)
                                                         .map(Object::toString)
                                                         .toArray(String[]::new));
        }
    }

    private void initializeLanguageManager() {
        lang = new LanguageManager(this, "languages", "zh");
        lang.setPlaceholderPrefix("{");
        lang.setPlaceholderSuffix("}");
        lang.setProvider(sender -> {
            if (sender instanceof Player)
                return ((Player) sender).getLocale();
            return null;
        });
    }

    private void initializeModules() {
        new MobArenaAddon();
        new BetterPortals();
        new FoundOres();
        new DeathLogger();
        new BetterBees();
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
            return;
        }
        manager.getLocales().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        manager.getCommandReplacements().addReplacement("moe", "moe|mu|moeutils");
        manager.registerDependency(MagicTime.class, magicTime);
        manager.registerDependency(MagicWeather.class, magicWeather);
        manager.registerCommand(new CommandHandler());
    }

    private Permission getPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        return permissionProvider != null ? permissionProvider.getProvider() : null;
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return economyProvider != null ? economyProvider.getProvider() : null;
    }

    private Chat getChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        return chatProvider != null ? chatProvider.getProvider() : null;
    }

    private Essentials getEssentials() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Essentials");
        return plugin != null ? (Essentials) plugin : null;
    }

}
