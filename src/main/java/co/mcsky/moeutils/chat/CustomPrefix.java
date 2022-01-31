package co.mcsky.moeutils.chat;

import co.mcsky.moecore.experience.ExperienceUtils;
import co.mcsky.moecore.luckperms.LuckPermsUtil;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.text3.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Modifies a player's prefix using LuckPerms.
 */
public class CustomPrefix {

    private final MoeUtils plugin;

    public CustomPrefix() {
        plugin = MoeUtils.plugin;
    }

    public void set(Player player, String prefix) {
        // make sure the color code in string test is \u00A7
        String test = Text.decolorize(prefix);

        // check length
        if (ChatColor.stripColor(Text.colorize(test)).length() > MoeUtils.config().prefix_max_length) {
            player.sendMessage(MoeUtils.text("prefix.prefix-too-long", "max", MoeUtils.config().prefix_max_length));
            return;
        }

        // check disabled formatting codes
        for (String k : MoeUtils.config().prefix_disabled_formatting_codes) {
            if (test.toUpperCase().contains("&" + k.toUpperCase())) {
                player.sendMessage(MoeUtils.text("common.blocked-format-codes", "code", "#" + k));
                return;
            }
        }

        // check blocked words
        for (String k : MoeUtils.config().prefix_blocked_words) {
            if (test.toUpperCase().contains(k.toUpperCase())) {
                player.sendMessage(MoeUtils.text("common.blocked-words", "word", k));
                return;
            }
        }

        // check exp cost
        if (MoeUtils.config().prefix_exp_cost > 0 && ExperienceUtils.getExpPoints(player) < MoeUtils.config().prefix_exp_cost) {
            player.sendMessage(MoeUtils.text("common.not-enough-exp"));
            return;
        }

        // check & charge money cost
        if (MoeUtils.config().prefix_money_cost > 0) {
            if (!MoeUtils.economy().has(player, MoeUtils.config().prefix_money_cost)) {
                player.sendMessage(MoeUtils.text("common.not-enough-money"));
                return;
            }
        }
        MoeUtils.economy().withdrawPlayer(player, MoeUtils.config().prefix_money_cost);

        // all check passed, then apply the prefix

        // charge exp
        if (MoeUtils.config().prefix_exp_cost > 0) {
            ExperienceUtils.subtractExpPoints(player, MoeUtils.config().prefix_exp_cost);
        }

        test = Text.colorize(test);
        LuckPermsUtil.userSetPrefixAsync(player.getUniqueId(), test, MoeUtils.config().prefix_priority);
        player.sendMessage(MoeUtils.text("prefix.set-success", "prefix", test));
    }

    public void clear(Player player) {
        LuckPermsUtil.userRemovePrefixAsync(player.getUniqueId());
        player.sendMessage(MoeUtils.text("prefix.clear-success"));
    }
}
