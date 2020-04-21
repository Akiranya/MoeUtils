package co.mcsky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.mcsky.LanguageManager;
import co.mcsky.MoeUtils;
import co.mcsky.magicutils.MagicTime;
import co.mcsky.magicutils.TimeOption;
import org.bukkit.entity.Player;

@CommandAlias("mu|moe|moeutils")
public class MagicTimeCommand extends BaseCommand {

    private final MoeUtils moe;
    private final LanguageManager lm;
    private final MagicTime magicTime;

    public MagicTimeCommand(MoeUtils moe) {
        this.moe = moe;
        this.lm = moe.languageManager;
        this.magicTime = new MagicTime(moe);
    }

    @Subcommand("time day")
    @CommandPermission("moe.magic.time")
    @Description("Call magic day.")
    public void callDay(Player player) {
        magicTime.call(TimeOption.DAY, player);
    }

    @Subcommand("time night")
    @CommandPermission("moe.magic.time")
    @Description("Call magic night.")
    public void callNight(Player player) {
        magicTime.call(TimeOption.NIGHT, player);
    }

    @Subcommand("time reset")
    @CommandPermission("moe.magic.reset")
    @Description("Reset cooldown of magic time.")
    public void reset(Player player) {
        magicTime.resetCooldown();
        player.sendMessage(lm.common_reset);
    }

    @Subcommand("time status")
    @CommandPermission("moe.magic.status")
    @Description("Get the last player who called magic time.")
    public void status(Player player) {
        player.sendMessage(magicTime.getLastPlayer());
    }

}
