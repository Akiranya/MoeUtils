package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;

@CommandAlias("%moe")
public class RootCommand extends BaseCommand {

    @Dependency
    private MoeUtils moe;
    @Dependency
    private LanguageManager lm;

    @HelpCommand
    @Description("Show help menu.")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload() {
        long elapsed = moe.reload();
        getCurrentCommandIssuer().sendMessage(String.format(lm.common_reloaded, elapsed));
    }

    @Subcommand("version|ver|v")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version() {
        getCurrentCommandIssuer().sendMessage(
                String.format(lm.common_version, moe.getDescription().getVersion()));
    }

}
