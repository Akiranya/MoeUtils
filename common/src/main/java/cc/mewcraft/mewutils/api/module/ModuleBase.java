package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.configurate.yaml.YamlConfigurationLoader;
import cc.mewcraft.mewcore.message.Translations;
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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

public abstract class ModuleBase
    implements TerminableConsumer, ModuleLogger, ModuleRequirement {

    private final MewPlugin plugin;
    private Path moduleDirectory;
    private CompositeTerminable terminableRegistry;
    private Translations lang;
    private YamlConfigurationLoader config;

    @Inject
    public ModuleBase(MewPlugin parent) {
        this.plugin = parent;
    }

    protected void load() {}

    protected void enable() {}

    protected void disable() {}

    public final void onLoad() {
        this.terminableRegistry = CompositeTerminable.create();

        this.moduleDirectory = Paths.get(this.plugin.getDataFolder().getPath()).resolve("modules").resolve(getId());
        if (this.moduleDirectory.toFile().mkdirs()) {
            info("module directory does not exist - creating one");
        }

        this.lang = new Translations(this.plugin, this.moduleDirectory.resolve("lang").toString(), "zh");
        this.config = YamlConfigurationLoader.builder()
            .file(this.moduleDirectory.resolve("config.yml").toFile())
            .indent(2)
            .build();

        // call subclass
        load();
    }

    public final void onEnable() {
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

    public final void onDisable() {
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

    public final YamlConfigurationLoader getConfig() {
        return this.config;
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
    public final @NonNull MewPlugin getPlugin() {
        return this.plugin;
    }

}
