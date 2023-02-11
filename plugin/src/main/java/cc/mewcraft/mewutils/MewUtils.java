package cc.mewcraft.mewutils;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import cc.mewcraft.mewutils.module.betterbeehive.BetterBeehiveModule;
import cc.mewcraft.mewutils.module.betterportal.BetterPortalModule;
import cc.mewcraft.mewutils.module.deathlogger.DeathLoggerModule;
import cc.mewcraft.mewutils.module.dropoverflow.DropOverflowModule;
import cc.mewcraft.mewutils.module.elytralimiter.ElytraLimiterModule;
import cc.mewcraft.mewutils.module.fireballutility.FireballModule;
import cc.mewcraft.mewutils.module.furnituredyer.FurnitureModule;
import cc.mewcraft.mewutils.module.oreannouncer.OreAnnouncerModule;
import cc.mewcraft.mewutils.module.slimeutility.SlimeUtilityModule;
import cc.mewcraft.mewutils.module.villagerutility.VillagerUtilityModule;
import cc.mewcraft.mewutils.placeholder.MewUtilsExpansion;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MewUtils extends ExtendedJavaPlugin implements MewPlugin {

    public static MewUtils INSTANCE;

    private MewConfig config; // main config
    private Translations translations; // main translations
    private Economy economy;
    private List<ModuleBase> modules;
    private CommandRegistry commandRegistry;

    public static void debug(String message) {
        if (MewUtils.config().debug) INSTANCE.getLogger().warning("[DEBUG] " + message);
    }

    public static void debug(Throwable message) {
        if (MewUtils.config().debug) INSTANCE.getLogger().warning("[DEBUG] " + message.getMessage());
    }

    public static void log(final String msg) {
        INSTANCE.getLogger().info(msg);
    }

    public static MewConfig config() {
        return INSTANCE.config;
    }

    public static Translations translations() {
        return INSTANCE.translations;
    }

    public static Economy economy() {
        return INSTANCE.economy;
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    private void hookExternal() {
        if (HookChecker.hasPlaceholderAPI()) {
            new MewUtilsExpansion().register();
            MewUtils.log("Hooked into PlaceholderAPI");
        }
    }

    @Override
    protected void load() {
        try {
            this.commandRegistry = new CommandRegistry(this);
            prepareInternalCommands();
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands! See the stacktrace below for more details");
            e.printStackTrace();
        }

        this.modules = new ArrayList<>();
    }

    @Override
    protected void enable() {
        INSTANCE = this;

        try {
            this.economy = Services.load(Economy.class);
        } catch (Exception e) {
            getLogger().severe("Failed to hook into Vault! See the stacktrace below for more details");
            e.printStackTrace();
        }

        this.translations = new Translations(this);
        this.config = new MewConfig();
        this.config.load();
        this.config.save();

        // --- Configure guice ---

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Plugin.class).toInstance(MewUtils.this);
                bind(MewPlugin.class).toInstance(MewUtils.this);
                bind(JavaPlugin.class).toInstance(MewUtils.this);
            }
        });

        // --- Load modules ---

        this.modules.add(injector.getInstance(BetterBeehiveModule.class));
        this.modules.add(injector.getInstance(BetterPortalModule.class));
        this.modules.add(injector.getInstance(DeathLoggerModule.class));
        this.modules.add(injector.getInstance(ElytraLimiterModule.class));
        this.modules.add(injector.getInstance(FireballModule.class));
        this.modules.add(injector.getInstance(FurnitureModule.class));
        this.modules.add(injector.getInstance(DropOverflowModule.class));
        this.modules.add(injector.getInstance(OreAnnouncerModule.class));
        this.modules.add(injector.getInstance(SlimeUtilityModule.class));
        this.modules.add(injector.getInstance(VillagerUtilityModule.class));

        for (ModuleBase module : this.modules) {
            module.onLoad();
            module.onEnable();
        }

        // --- Make all commands effective ---
        this.commandRegistry.registerCommands();
    }

    @Override
    protected void disable() {
        this.modules.forEach(ModuleBase::onDisable);
    }

    @Override
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
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
                MewUtils.translations().of("common.reloaded").send(sender);
            }).build()
        );
    }

}
