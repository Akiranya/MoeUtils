package cc.mewcraft.mewutils;

import cc.mewcraft.lib.configurate.ConfigurateException;
import cc.mewcraft.lib.configurate.ConfigurationNode;
import cc.mewcraft.lib.configurate.yaml.YamlConfigurationLoader;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import cc.mewcraft.mewutils.module.betterbeehive.BetterBeehiveModule;
import cc.mewcraft.mewutils.module.betterportal.BetterPortalModule;
import cc.mewcraft.mewutils.module.deathlogger.DeathLoggerModule;
import cc.mewcraft.mewutils.module.dropoverflow.DropOverflowModule;
import cc.mewcraft.mewutils.module.elytralimiter.ElytraLimiterModule;
import cc.mewcraft.mewutils.module.fireballutils.FireballUtilsModule;
import cc.mewcraft.mewutils.module.furnituredye.FurnitureDyeModule;
import cc.mewcraft.mewutils.module.oreannouncer.OreAnnouncerModule;
import cc.mewcraft.mewutils.module.packetfilter.PacketFilterModule;
import cc.mewcraft.mewutils.module.slimeutils.SlimeUtilsModule;
import cc.mewcraft.mewutils.module.stringreplacer.StringReplacerModule;
import cc.mewcraft.mewutils.module.villagerutils.VillagerUtilsModule;
import cc.mewcraft.mewutils.util.Log;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class MewUtils extends ExtendedJavaPlugin implements MewPlugin {

    public static MewUtils INSTANCE;

    // --- data ---
    private ConfigurationNode configNode; // main config
    private Translations translations; // main translations

    // --- hooks ---
    // private Economy economy;

    // --- modules ---
    private List<ModuleBase> modules;

    // --- commands ---
    private CommandRegistry commandRegistry;

    // --- variables ---
    private boolean verbose;

    // public static Economy economy() {
    //     return INSTANCE.economy;
    // }

    public boolean devMode() {
        return this.verbose;
    }

    @Override
    protected void enable() {
        INSTANCE = this;

        getComponentLogger().info(text("Enabling...").color(NamedTextColor.AQUA));

        // --- Load main translations ---

        this.translations = new Translations(this);

        // --- Load main config ---

        saveDefaultConfig();
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(getDataFolder().toPath().resolve("config.yml").toFile()).indent(2).build();
            this.configNode = loader.load();
            this.verbose = this.configNode.node("verbose").getBoolean();
        } catch (ConfigurateException e) {
            getLogger().severe("Failed to load main config! See the stacktrace below for more details");
            e.printStackTrace();
        }

        // --- Initialise commands ---

        try {
            this.commandRegistry = new CommandRegistry(this);
            prepareInternalCommands();
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands! See the stacktrace below for more details");
            e.printStackTrace();
            return;
        }

        // --- Hook 3rd party ---

        // try {
        //     this.economy = Services.load(Economy.class);
        // } catch (Exception e) {
        //     Log.severe("Failed to hook into Vault! See the stacktrace below for more details");
        //     e.printStackTrace();
        // }

        // --- Configure guice ---

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Plugin.class).toInstance(MewUtils.this);
                bind(MewPlugin.class).toInstance(MewUtils.this);
                bind(JavaPlugin.class).toInstance(MewUtils.this);
            }
        });

        // --- Load modules ---

        this.modules = new ArrayList<>();
        this.modules.add(injector.getInstance(BetterBeehiveModule.class));
        this.modules.add(injector.getInstance(BetterPortalModule.class));
        this.modules.add(injector.getInstance(DeathLoggerModule.class));
        this.modules.add(injector.getInstance(ElytraLimiterModule.class));
        this.modules.add(injector.getInstance(FireballUtilsModule.class));
        this.modules.add(injector.getInstance(FurnitureDyeModule.class));
        this.modules.add(injector.getInstance(DropOverflowModule.class));
        this.modules.add(injector.getInstance(OreAnnouncerModule.class));
        this.modules.add(injector.getInstance(SlimeUtilsModule.class));
        this.modules.add(injector.getInstance(VillagerUtilsModule.class));
        this.modules.add(injector.getInstance(PacketFilterModule.class));
        this.modules.add(injector.getInstance(StringReplacerModule.class));

        for (ModuleBase module : this.modules) {
            try {
                module.onLoad();
                module.onEnable();
            } catch (Exception e) {
                Log.severe("Module " + module.getId() + " failed to load/enable! Check the stacktrace below for more details");
                e.printStackTrace();
            }
        }

        // --- Make all commands effective ---

        this.commandRegistry.registerCommands();
    }

    @Override
    protected void disable() {
        getComponentLogger().info(text("Disabling...").color(NamedTextColor.AQUA));

        for (ModuleBase module : this.modules) {
            try {
                module.onDisable();
            } catch (Exception e) {
                Log.severe("Module " + module.getId() + " failed to disbale! Check the stacktrace below for more details");
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    @Override
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    @Override public ClassLoader getClassLoader0() {
        return getClassLoader();
    }

    @Override public Translations getLang() {
        return this.translations;
    }

    @Override public ConfigurationNode getConfigNode() {
        return this.configNode;
    }

    private void prepareInternalCommands() {
        // for now, it's just a reload command
        this.commandRegistry.prepareCommand(this.commandRegistry
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("reload")
            .handler(context -> {
                CommandSender sender = context.getSender();
                reload();
                getLang().of("reloaded").send(sender);
            }).build()
        );
    }

    // private void hookPlaceholderAPI() {
    //     if (HookChecker.hasPlaceholderAPI()) {
    //         new MewUtilsExpansion().register();
    //         Log.info("Hooked into PlaceholderAPI");
    //     }
    // }

}
