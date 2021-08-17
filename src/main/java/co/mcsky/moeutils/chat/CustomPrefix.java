package co.mcsky.moeutils.chat;

import co.mcsky.moecore.MoeCore;
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
        String test = prefix.replace("§", "");

        // check length
        if (ChatColor.stripColor(Text.colorize(test)).length() > plugin.config.prefix_max_length) {
            player.sendMessage(plugin.message(player, "prefix.prefix-too-long", "max", plugin.config.prefix_max_length));
            return;
        }

        // check disabled formatting codes
        for (String k : plugin.config.prefix_disabled_formatting_codes) {
            if (test.toUpperCase().contains("&" + k.toUpperCase())) {
                player.sendMessage(plugin.message(player, "common.blocked-format-codes", "code", "#" + k));
                return;
            }
        }

        // check blocked words
        for (String k : plugin.config.prefix_blocked_words) {
            if (test.toUpperCase().contains(k.toUpperCase())) {
                player.sendMessage(plugin.message(player, "common.blocked-words", "word", k));
                return;
            }
        }

        // check exp cost
        if (plugin.config.prefix_exp_cost > 0 && ExperienceUtils.getExpPoints(player) < plugin.config.prefix_exp_cost) {
            player.sendMessage(plugin.message(player, "common.not-enough-exp"));
            return;
        }

        // check & charge money cost
        if (plugin.config.prefix_money_cost > 0) {
            if (!MoeUtils.economy.has(player, plugin.config.prefix_money_cost)) {
                player.sendMessage(plugin.message(player, "common.not-enough-money"));
                return;
            }
            MoeCore.plugin.systemAccount().deposit(plugin.config.prefix_money_cost, plugin);
        }
        MoeUtils.economy.withdrawPlayer(player, plugin.config.prefix_money_cost);

        // all check passed, then apply the prefix

        // charge exp
        if (plugin.config.prefix_exp_cost > 0) {
            ExperienceUtils.subtractExpPoints(player, plugin.config.prefix_exp_cost);
        }

        test = Text.colorize(test);
        LuckPermsUtil.userSetPrefixAsync(player.getUniqueId(), test, plugin.config.prefix_priority);
        player.sendMessage(plugin.message(player, "prefix.set-success", "prefix", test));
    }

    public void clear(Player player) {
        LuckPermsUtil.userRemovePrefixAsync(player.getUniqueId());
        player.sendMessage(plugin.message(player, "prefix.clear-success"));
    }
}
