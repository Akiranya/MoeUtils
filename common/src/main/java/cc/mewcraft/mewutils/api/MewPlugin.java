package cc.mewcraft.mewutils.api;

import cc.mewcraft.mewutils.api.command.CommandRegistry;
import org.bukkit.plugin.Plugin;

public interface MewPlugin extends Plugin {

    CommandRegistry getCommandRegistry();

}
