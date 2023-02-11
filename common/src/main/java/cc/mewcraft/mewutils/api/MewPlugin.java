package cc.mewcraft.mewutils.api;

import cc.mewcraft.lib.configurate.ConfigurationNode;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import org.bukkit.plugin.Plugin;

public interface MewPlugin extends Plugin {

    CommandRegistry getCommandRegistry();

    ClassLoader getClassLoader0();

    Translations getLang();

    ConfigurationNode getConfigNode();

}
