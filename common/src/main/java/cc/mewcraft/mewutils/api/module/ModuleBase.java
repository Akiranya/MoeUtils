package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.configurate.CommentedConfigurationNode;
import cc.mewcraft.lib.configurate.yaml.YamlConfigurationLoader;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.util.UtilFile;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

@DefaultQualifier(NonNull.class)
public abstract class ModuleBase
    implements TerminableConsumer, ModuleLogger, ModuleRequirement {

    private final MewPlugin plugin;
    private final Path moduleDirectory;
    private final CompositeTerminable terminableRegistry;
    private final Translations lang;
    private final YamlConfigurationLoader config;
    private @MonotonicNonNull CommentedConfigurationNode configNode;

    @Inject
    public ModuleBase(MewPlugin parent) {
        this.plugin = parent;

        // backed closeable of this module
        this.terminableRegistry = CompositeTerminable.create();

        // create dedicated directory for this module
        this.moduleDirectory = this.plugin.getDataFolder().toPath().resolve("modules").resolve(getId());
        if (this.moduleDirectory.toFile().mkdirs()) {
            info("module directory does not exist - creating one");
        }

        // dedicated language files for this module
        this.lang = new Translations(this.plugin, Path.of("modules").resolve(getId()).resolve("lang").toString(), "zh");

        // dedicated config file for this module
        File configFile = this.moduleDirectory.resolve("config.yml").toFile();
        if (!configFile.exists()) {
            // copy default config.yml if not existing
            UtilFile.copyResourcesRecursively(parent.getClassLoader0().getResource("modules/" + getId() + "/config.yml"), configFile);
        }
        this.config = YamlConfigurationLoader.builder()
            .file(configFile)
            .indent(2)
            .build();
    }

    protected void load() throws Exception {}

    protected void enable() throws Exception {}

    protected void disable() throws Exception {}

    public final void onLoad() throws Exception {
        // load the config file into node
        this.configNode = this.config.load();

        // call subclass
        load();
    }

    public final void onEnable() throws Exception {
        if (!canEnable()) {
            warn(getId() + " is not enabled due to requirement not met");
            return;
        }

        // schedule cleanup of the registry
        Schedulers.builder()
            .async()
            .after(10, TimeUnit.SECONDS)
            .every(30, TimeUnit.SECONDS)
            .run(this.terminableRegistry::cleanup)
            .bindWith(this.terminableRegistry);

        // call subclass
        enable();

        this.plugin.getComponentLogger().info(text()
            .append(text(getId()).color(NamedTextColor.GOLD))
            .appendSpace().append(text("is enabled!"))
            .build()
        );
    }

    public final void onDisable() throws Exception {
        // call subclass
        disable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();

        this.plugin.getComponentLogger().info(text()
            .append(text(getId()).color(NamedTextColor.GOLD))
            .appendSpace().append(text("is disabled!"))
            .build()
        );
    }

    public final Translations getLang() {
        return this.lang;
    }

    public final YamlConfigurationLoader getConfigLoader() {
        return this.config;
    }

    public final CommentedConfigurationNode getConfigNode() {
        return requireNonNull(this.configNode);
    }

    public final Path getDataFolder() {
        return this.moduleDirectory.resolve("data");
    }

    public final Path getModuleFolder() {
        return this.moduleDirectory;
    }

    public final <T extends AutoCloseableListener> void registerListener(@NonNull T listener) {
        requireNonNull(listener, "listener");
        getPlugin().getServer().getPluginManager().registerEvents(
            bind(listener), getPlugin()
        );
    }

    public final void registerCommand(@NonNull Function<CommandRegistry, Command.Builder<CommandSender>> command) {
        requireNonNull(command, "command");
        CommandRegistry registry = getPlugin().getCommandRegistry();
        registry.prepareCommand(
            command.apply(registry).build()
        );
    }

    @Override
    public final <T extends TerminableModule> @NonNull T bindModule(@NonNull final T module) {
        requireNonNull(module, "module");
        return this.terminableRegistry.bindModule(module);
    }

    @Override
    public final <T extends AutoCloseable> @NonNull T bind(@NonNull final T terminable) {
        requireNonNull(terminable, "terminable");
        return this.terminableRegistry.bind(terminable);
    }

    @Override
    public final MewPlugin getPlugin() {
        return this.plugin;
    }

}