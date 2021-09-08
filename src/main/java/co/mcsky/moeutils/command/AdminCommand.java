package co.mcsky.moeutils.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.mcsky.moeutils.MoeUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("%main")
public class AdminCommand extends BaseCommand {
    @Dependency
    public MoeUtils plugin;

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload(CommandSender sender) {
        MoeUtils.plugin.reload();
        sender.sendMessage(MoeUtils.text("common.reloaded"));
    }

    @Subcommand("version")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version(CommandSender sender) {
        sender.sendMessage(MoeUtils.text("common.version", "version", plugin.getDescription().getVersion()));
    }
}

