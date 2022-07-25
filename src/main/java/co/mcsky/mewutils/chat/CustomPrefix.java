package co.mcsky.mewutils.chat;

import co.mcsky.mewcore.util.UtilExperience;
import co.mcsky.mewcore.util.UtilLuckPerms;
import co.mcsky.mewutils.MewUtils;
import me.lucko.helper.text3.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Modifies a player's prefix using LuckPerms.
 */
public class CustomPrefix {

    private final MewUtils plugin;

    public CustomPrefix() {
        plugin = MewUtils.p;
    }

    public void set(Player player, String prefix) {
        // make sure the color code in string test is \u00A7
        String test = Text.decolorize(prefix);

        // check length
        if (ChatColor.stripColor(Text.colorize(test)).length() > MewUtils.config().prefix_max_length) {
            player.sendMessage(MewUtils.text("prefix.prefix-too-long", "max", MewUtils.config().prefix_max_length));
            return;
        }

        // check disabled formatting codes
        for (String k : MewUtils.config().prefix_disabled_formatting_codes) {
            if (test.toUpperCase().contains("&" + k.toUpperCase())) {
                player.sendMessage(MewUtils.text("common.blocked-format-codes", "code", "#" + k));
                return;
            }
        }

        // check blocked words
        for (String k : MewUtils.config().prefix_blocked_words) {
            if (test.toUpperCase().contains(k.toUpperCase())) {
                player.sendMessage(MewUtils.text("common.blocked-words", "word", k));
                return;
            }
        }

        // check exp cost
        if (MewUtils.config().prefix_exp_cost > 0 && UtilExperience.getExpPoints(player) < MewUtils.config().prefix_exp_cost) {
            player.sendMessage(MewUtils.text("common.not-enough-exp"));
            return;
        }

        // check & charge money cost
        if (MewUtils.config().prefix_money_cost > 0) {
            if (!MewUtils.economy().has(player, MewUtils.config().prefix_money_cost)) {
                player.sendMessage(MewUtils.text("common.not-enough-money"));
                return;
            }
        }
        MewUtils.economy().withdrawPlayer(player, MewUtils.config().prefix_money_cost);

        // all check passed, then apply the prefix

        // charge exp
        if (MewUtils.config().prefix_exp_cost > 0) {
            UtilExperience.subtractExpPoints(player, MewUtils.config().prefix_exp_cost);
        }

        test = Text.colorize(test);
        UtilLuckPerms.userSetPrefixAsync(player.getUniqueId(), test, MewUtils.config().prefix_priority);
        player.sendMessage(MewUtils.text("prefix.set-success", "prefix", test));
    }

    public void clear(Player player) {
        UtilLuckPerms.userRemovePrefixAsync(player.getUniqueId());
        player.sendMessage(MewUtils.text("prefix.clear-success"));
    }
}
