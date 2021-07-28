package co.mcsky.moeutils;

import cat.nyaa.nyaacore.component.ISystemBalance;
import cat.nyaa.nyaacore.component.NyaaComponent;
import co.aikar.commands.PaperCommandManager;
import co.mcsky.moeutils.bees.BetterBees;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.misc.BetterPortals;
import co.mcsky.moeutils.misc.DeathLogger;
import co.mcsky.moeutils.misc.EndEyeChanger;
import co.mcsky.moeutils.misc.LoginProtector;
import co.mcsky.moeutils.util.CooldownManager;
import co.mcsky.moeutils.util.Timer;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.UUID;

public class MoeUtils extends JavaPlugin {

    public static MoeUtils plugin;
    public static Economy economy;
    public static ISystemBalance systemBalance;

    public MoeConfig config;
    public LanguageManager lang;
    public PaperCommandManager manager;

    private MagicTime magicTime;
    private MagicWeather magicWeather;


    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Hook into Vault
        economy = getEconomy();
        systemBalance = NyaaComponent.get(ISystemBalance.class);

        // Initialize language manager
        initializeLanguageManager();

        // Initialize config manager
        config = new MoeConfig();
        config.load();

        // Initialize functions & initialize config nodes
        // In this stage, config node is initialized (with default values if nothing is present in the file)
        initializeModules();

        // Register commands
        registerCommands();

        // Save nodes in config.yml
        config.save();
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
        try {
            new BetterPortals();
            new FoundOres();
            new DeathLogger();
            new BetterBees();
            new LoginProtector();
            new EndEyeChanger();
            magicTime = new MagicTime();
            magicWeather = new MagicWeather();
        } catch (SerializationException e) {
            getLogger().severe(e.getMessage());
        }

    }

    private void registerCommands() {
        manager = new PaperCommandManager(this);
        manager.registerDependency(MagicTime.class, magicTime);
        manager.registerDependency(MagicWeather.class, magicWeather);
        manager.registerCommand(new MoeCommands());
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return economyProvider != null ? economyProvider.getProvider() : null;
    }

}
