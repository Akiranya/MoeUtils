package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.*;
import co.mcsky.MoeUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("mu|moe|moeutils")
public class MoeUtilsCommand extends BaseCommand {

    private final MoeUtils moe;

    public MoeUtilsCommand(MoeUtils moe) {
        this.moe = moe;
    }

    @HelpCommand
    @Description("Show the help menu of this plugin.")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload(CommandSender sender) {
        sender.sendMessage(String.format(moe.commonCfg.msg_reloaded, moe.reload()));
    }

    @Subcommand("version|ver")
    @CommandPermission("moe.admin")
    @Description("See version.")
    public void version() {
        CommandManager.getCurrentCommandIssuer().sendMessage(
                String.format(moe.commonCfg.msg_version, moe.getDescription().getVersion()));
    }

}
