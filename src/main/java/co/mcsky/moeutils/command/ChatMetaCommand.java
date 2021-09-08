package co.mcsky.moeutils.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.chat.CustomPrefix;
import co.mcsky.moeutils.chat.CustomSuffix;
import org.bukkit.entity.Player;

@CommandAlias("%main")
public class ChatMetaCommand extends BaseCommand {
    @Dependency
    public CustomPrefix customPrefix;
    @Dependency
    public CustomSuffix customSuffix;

    @Subcommand("prefix")
    @CommandCompletion("@nothing")
    @CommandPermission("moe.prefix")
    public void prefix(Player player, String prefix) {
        customPrefix.set(player, prefix);
    }

    @Subcommand("prefix clear")
    @CommandPermission("moe.prefix")
    public void prefix(Player player) {
        customPrefix.clear(player);
    }

    @Subcommand("suffix")
    @CommandCompletion("@nothing")
    @CommandPermission("moe.suffix")
    public void suffix(Player player, String suffix) {
        customSuffix.set(player, suffix);
    }

    @Subcommand("suffix clear")
    @CommandPermission("moe.suffix")
    public void suffix(Player player) {
        customSuffix.clear(player);
    }

}
