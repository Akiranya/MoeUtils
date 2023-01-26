package cc.mewcraft.mewutils.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.commandframework.arguments.flags.CommandFlag;
import cc.mewcraft.lib.commandframework.brigadier.CloudBrigadierManager;
import cc.mewcraft.lib.commandframework.bukkit.CloudBukkitCapabilities;
import cc.mewcraft.lib.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cc.mewcraft.lib.commandframework.keys.CloudKey;
import cc.mewcraft.lib.commandframework.keys.SimpleCloudKey;
import cc.mewcraft.lib.commandframework.minecraft.extras.AudienceProvider;
import cc.mewcraft.lib.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cc.mewcraft.lib.commandframework.paper.PaperCommandManager;
import cc.mewcraft.lib.geantyref.TypeToken;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.command.command.FireballCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@NonNull
public class CommandManager extends PaperCommandManager<CommandSender> {

    public static final CloudKey<MewUtils> PLUGIN = SimpleCloudKey.of("mewutils:plugin", TypeToken.get(MewUtils.class));
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public CommandManager(MewUtils plugin) throws Exception {
        super(
            plugin,
            AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build(),
            Function.identity(),
            Function.identity()
        );

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Register Asynchronous Completion Listener ----
        if (hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            registerAsynchronousCompletions();
            plugin.getLogger().info("Successfully registered asynchronous command completion listener.");
        }

        // ---- Inject instances into the command context ----
        this.registerCommandPreProcessor(ctx -> ctx.getCommandContext().store(PLUGIN, plugin));

        // ---- Change default exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));

        // ---- Register all commands ----
        Stream.of(
            new FireballCommand(this)
        ).forEach(AbstractCommand::register);
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        flagRegistry.put(name, flagBuilder);
    }

    @SafeVarargs
    public final void register(final Command<CommandSender>... commands) {
        Arrays.asList(commands).forEach(this::command);
    }

}
