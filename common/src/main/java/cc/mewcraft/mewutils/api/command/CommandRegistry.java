package cc.mewcraft.mewutils.api.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.commandframework.arguments.flags.CommandFlag;
import cc.mewcraft.lib.commandframework.brigadier.CloudBrigadierManager;
import cc.mewcraft.lib.commandframework.bukkit.CloudBukkitCapabilities;
import cc.mewcraft.lib.commandframework.execution.CommandExecutionCoordinator;
import cc.mewcraft.lib.commandframework.minecraft.extras.AudienceProvider;
import cc.mewcraft.lib.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cc.mewcraft.lib.commandframework.paper.PaperCommandManager;
import cc.mewcraft.mewutils.api.MewPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;

public class CommandRegistry extends PaperCommandManager<CommandSender> {

    private final Map<String, CommandFlag.Builder<?>> flagRegistry;
    private final List<Command<CommandSender>> preparedCommands;

    public CommandRegistry(JavaPlugin plugin) throws Exception {
        super(
            plugin,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(),
            Function.identity()
        );

        this.flagRegistry = new HashMap<>();
        this.preparedCommands = new ArrayList<>();

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Setup exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    /**
     * Adds specific command which will be registered upon the main plugin ({@link MewPlugin}) enabling.
     *
     * @param command the command to be registered
     */
    public final void prepareCommand(final Command<CommandSender> command) {
        this.preparedCommands.add(command);
    }

    @SafeVarargs
    public final void prepareCommands(final Command<CommandSender>... commands) {
        this.preparedCommands.addAll(Arrays.asList(commands));
    }

    /**
     * Registers all added commands.
     * <p>
     * This method will make the added commands effective.
     */
    public final void registerCommands() {
        for (final Command<CommandSender> added : this.preparedCommands) command(added);
    }

}
