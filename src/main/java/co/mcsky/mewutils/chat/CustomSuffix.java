package co.mcsky.mewutils.chat;

import co.mcsky.mewcore.util.UtilExperience;
import co.mcsky.mewcore.util.UtilLuckPerms;
import co.mcsky.mewutils.MewUtils;
import me.lucko.helper.text3.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Modifies a player's suffix using LuckPerms.
 */
public class CustomSuffix {

    private final MewUtils plugin;

    public CustomSuffix() {
        plugin = MewUtils.p;
    }

    public void set(Player player, String suffix) {
        // make sure the color code in string test is \u00A7
        String test = Text.decolorize(suffix);

        // check length
        if (ChatColor.stripColor(Text.colorize(test)).length() > MewUtils.config().suffix_max_length) {
            player.sendMessage(MewUtils.text("suffix.suffix-too-long", "max", MewUtils.config().suffix_max_length));
            return;
        }

        // check disabled formatting codes
        for (String k : MewUtils.config().suffix_disabled_formatting_codes) {
            if (test.toUpperCase().contains("&" + k.toUpperCase())) {
                // TODO not working
                player.sendMessage(MewUtils.text("common.blocked-format-codes", "code", "#" + k));
                return;
            }
        }

        // check blocked words
        for (String k : MewUtils.config().suffix_blocked_words) {
            if (test.toUpperCase().contains(k.toUpperCase())) {
                player.sendMessage(MewUtils.text("common.blocked-words", "word", k));
                return;
            }
        }

        // check exp cost
        if (MewUtils.config().suffix_exp_cost > 0 && UtilExperience.getExpPoints(player) < MewUtils.config().suffix_exp_cost) {
            player.sendMessage(MewUtils.text("common.not-enough-exp"));
            return;
        }

        // check & charge money cost
        if (MewUtils.config().suffix_money_cost > 0) {
            if (!MewUtils.economy().has(player, MewUtils.config().suffix_money_cost)) {
                player.sendMessage(MewUtils.text("common.not-enough-money"));
                return;
            }
        }
        MewUtils.economy().withdrawPlayer(player, MewUtils.config().suffix_money_cost);

        // all check passed, then apply the suffix

        // charge exp
        if (MewUtils.config().suffix_exp_cost > 0) {
            UtilExperience.subtractExpPoints(player, MewUtils.config().suffix_exp_cost);
        }

        test = Text.colorize(test);
        UtilLuckPerms.userSetSuffixAsync(player.getUniqueId(), test, MewUtils.config().suffix_priority);
        player.sendMessage(MewUtils.text("suffix.set-success", "suffix", test));
    }

    public void clear(Player player) {
        UtilLuckPerms.userRemoveSuffixAsync(player.getUniqueId());
        player.sendMessage(MewUtils.text("suffix.clear-success"));
    }
}
