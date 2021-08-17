package co.mcsky.moeutils.chat;

import co.mcsky.moecore.MoeCore;
import co.mcsky.moecore.experience.ExperienceUtils;
import co.mcsky.moecore.luckperms.LuckPermsUtil;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.text3.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class CustomSuffix {

    private final MoeUtils plugin;

    public CustomSuffix() {
        plugin = MoeUtils.plugin;
    }

    public void set(Player player, String suffix) {
        String test = suffix.replace("§", "");

        // check length
        if (ChatColor.stripColor(Text.colorize(test)).length() > plugin.config.suffix_max_length) {
            player.sendMessage(plugin.getMessage(player, "suffix.suffix-too-long", "max", plugin.config.suffix_max_length));
            return;
        }

        // check disabled formatting codes
        for (String k : plugin.config.suffix_disabled_formatting_codes) {
            if (test.toUpperCase().contains("&" + k.toUpperCase())) {
                player.sendMessage(plugin.getMessage(player, "common.blocked-format-codes", "code", "#" + k));
                return;
            }
        }

        // check blocked words
        for (String k : plugin.config.suffix_blocked_words) {
            if (test.toUpperCase().contains(k.toUpperCase())) {
                player.sendMessage(plugin.getMessage(player, "common.blocked-words", "word", k));
                return;
            }
        }

        // check exp cost
        if (plugin.config.suffix_exp_cost > 0 && ExperienceUtils.getExpPoints(player) < plugin.config.suffix_exp_cost) {
            player.sendMessage(plugin.getMessage(player, "common.not-enough-exp"));
            return;
        }

        // check & charge money cost
        if (plugin.config.suffix_money_cost > 0) {
            if (!MoeUtils.economy.has(player, plugin.config.suffix_money_cost)) {
                player.sendMessage(plugin.getMessage(player, "common.not-enough-money"));
                return;
            }
            MoeCore.plugin.systemAccount().deposit(plugin.config.suffix_money_cost, plugin);
        }
        MoeUtils.economy.withdrawPlayer(player, plugin.config.suffix_money_cost);

        // all check passed, then apply the suffix

        // charge exp
        if (plugin.config.suffix_exp_cost > 0) {
            ExperienceUtils.subtractExpPoints(player, plugin.config.suffix_exp_cost);
        }

        test = Text.colorize(test);
        LuckPermsUtil.userSetSuffixAsync(player.getUniqueId(), test, plugin.config.suffix_priority);
        player.sendMessage(plugin.getMessage(player, "suffix.set-success", "suffix", test));
    }

    public void clear(Player player) {
        LuckPermsUtil.userRemoveSuffixAsync(player.getUniqueId());
        player.sendMessage(plugin.getMessage(player, "suffix.clear-success"));
    }
}
