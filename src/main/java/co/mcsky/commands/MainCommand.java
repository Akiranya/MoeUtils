package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.*;
import co.mcsky.MoeUtils;
import co.mcsky.LanguageManager;
import org.bukkit.command.CommandSender;

@CommandAlias("mu|moe|moeutils")
public class MainCommand extends BaseCommand {

    private final MoeUtils moe;
    private final LanguageManager lm;

    public MainCommand(MoeUtils moe) {
        this.moe = moe;
        this.lm = moe.languageManager;
    }

    @HelpCommand
    @Description("Show help menu.")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("moe.admin")
    @Description("Reload config from files.")
    public void reload(CommandSender sender) {
        long elapsed = moe.reload();
        sender.sendMessage(String.format(lm.common_reloaded, elapsed));
    }

    @Subcommand("version|ver")
    @CommandPermission("moe.admin")
    @Description("Get the version of this plugin.")
    public void version() {
        CommandManager.getCurrentCommandIssuer().sendMessage(
                String.format(lm.common_version, moe.getDescription().getVersion()));
    }

    /* testing */

//    @Subcommand("a")
//    @Description("Test input.")
//    public void a(String someText, int someInt, double someDouble, World someWorld) {
//        getCurrentCommandIssuer().sendMessage(someText + ", " +
//                                              someInt + ", " +
//                                              someDouble + ", " +
//                                              someWorld);
//    }
//
//    @Subcommand("b")
//    @Description("Test completion.")
//    @Syntax("[int] [double]")
//    //    @CommandCompletion("@players @players @worlds")
//    public void b(int someInt, double someDouble, String someText) {
//        getCurrentCommandIssuer().sendMessage(someText);
//    }

}
