package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.mewutils.api.MewPlugin;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public interface ModuleLogger extends ModuleIdentifier {

    MewPlugin getPlugin();

    // --- plain string logger ---

    default void info(String msg) {
        getPlugin().getLogger().info(msg);
    }

    default void warn(String msg) {
        getPlugin().getLogger().warning(msg);
    }

    default void error(String msg) {
        getPlugin().getLogger().severe(msg);
    }

    // --- component logger ---

    default void info(Component msg) {
        getPlugin().getComponentLogger().info(text("[" + getId() + "] ").append(msg));
    }

    default void warn(Component msg) {
        getPlugin().getComponentLogger().warn(text("[" + getId() + "] ").append(msg));
    }

    default void error(Component msg) {
        getPlugin().getComponentLogger().error(text("[" + getId() + "] ").append(msg));
    }

}
