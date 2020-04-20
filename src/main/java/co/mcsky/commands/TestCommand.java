package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.mcsky.MoeUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandAlias("test")
public class TestCommand extends BaseCommand {

    private final MoeUtils moe;

    public TestCommand(MoeUtils moe) {
        this.moe = moe;
    }

    @Subcommand("input")
    @Description("Test input.")
    public void input(String someText, int someInt, double someDouble, World someWorld) {
        getCurrentCommandIssuer().sendMessage(someText + ", " +
                                              someInt + ", " +
                                              someDouble + ", " +
                                              someWorld);
    }

    @Subcommand("completion")
    @Description("Test completion.")
    @CommandCompletion("@players @players @worlds")
    public void completion(int someInt, double someDouble, Player somePlayer) {
        getCurrentCommandIssuer().sendMessage(String.valueOf(someInt));
    }

}
