package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.mewutils.api.MewPlugin;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public interface ModuleLogger extends ModuleIdentifier {

    MewPlugin getPlugin();

    default String getPrefix() {
        return "[" + getId() + "] ";
    }

    // --- plain string logger ---

    default void info(String msg) {
        getPlugin().getLogger().info(getPrefix() + msg);
    }

    default void warn(String msg) {
        getPlugin().getLogger().warning(getPrefix() + msg);
    }

    default void error(String msg) {
        getPlugin().getLogger().severe(getPrefix() + msg);
    }

    // --- component logger ---

    default void info(Component msg) {
        getPlugin().getComponentLogger().info(text(getPrefix()).append(msg));
    }

    default void warn(Component msg) {
        getPlugin().getComponentLogger().warn(text(getPrefix()).append(msg));
    }

    default void error(Component msg) {
        getPlugin().getComponentLogger().error(text(getPrefix()).append(msg));
    }

}
