package co.mcsky.moeutils.magicutils;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.utilities.CooldownManager;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This class shares code for {@link MagicTime} and {@link MagicWeather}.
 */
public abstract class MagicBase {

    public final MoeUtils plugin;
    public final int COOLDOWN_DURATION;

    MagicBase(MoeUtils plugin, int cooldownDuration) {
        this.COOLDOWN_DURATION = cooldownDuration;
        this.plugin = plugin;
    }

    /**
     * Check if the specific player has their cooldown ready. If the cooldown
     * were NOT ready, it would send relevant messages to the player. Messages
     * will not be sent if the cooldown is ready.
     *
     * @param player       The player you want to check cooldown.
     * @param COOLDOWN_KEY The cooldown key and this should be unique.
     *
     * @return True if the cooldown is ready. False else wise.
     */
    boolean checkCooldown(Player player, UUID COOLDOWN_KEY) {
        if (CooldownManager.check(COOLDOWN_KEY, COOLDOWN_DURATION))
            return true;
        player.sendMessage(plugin.getMessage(player, "common.cooldown",
                                             "time", String.valueOf(CooldownManager.remaining(COOLDOWN_KEY, COOLDOWN_DURATION))));
        return false;
    }

    /**
     * Check if the specific player has sufficient balance. If the balance is
     * NOT sufficient, it would send relevant messages to the player. Messages
     * will not be sent if the player has sufficient balance.
     *
     * @param player The player who is to be charged.
     * @param cost   How much we charge the player.
     *
     * @return True if the player has sufficient balance. False else wise.
     */
    boolean checkBalance(Player player, int cost) {
        if (MoeUtils.economy.has(player, cost))
            return true;
        player.sendMessage(plugin.getMessage(player, "common.not_enough_money"));
        return false;
    }

    /**
     * Try to charge player with specific fee. If the charging is successful, it
     * will send relevant messages to the player.
     *
     * @param player The player who is to be charged.
     * @param cost   The amount of fee applied to the specific player.
     */
    void chargePlayer(Player player, int cost) {
        MoeUtils.economy.withdrawPlayer(player, cost);
        player.sendMessage(plugin.getMessage(player, "common.price", "cost", String.valueOf(cost)));
    }

}
