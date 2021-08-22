package co.mcsky.moeutils.magic;

import co.mcsky.moecore.MoeCore;
import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Shares code for {@link MagicTime} and {@link MagicWeather}.
 */
public abstract class MagicBase implements TerminableModule {

    public final int cooldownAmount;
    public final CooldownMap<UUID> cooldownMap;

    MagicBase(int cooldownAmount) {
        this.cooldownAmount = cooldownAmount;
        this.cooldownMap = CooldownMap.create(Cooldown.of(cooldownAmount, TimeUnit.SECONDS));
    }

    /**
     * Checks if the specific player has their cooldown ready. If the cooldown
     * were NOT ready, it would send relevant messages to the player. Messages
     * will not be sent if the cooldown is ready.
     *
     * @param player      The player you want to check cooldown.
     * @param cooldownKey The cooldown key and this should be unique.
     * @return True if the cooldown is ready. False else wise.
     */
    boolean testSilently(Player player, UUID cooldownKey) {
        if (cooldownMap.testSilently(cooldownKey))
            return true;
        player.sendMessage(MoeUtils.text("common.cooldown", "time", cooldownMap.remainingTime(cooldownKey, TimeUnit.SECONDS)));
        return false;
    }

    /**
     * Resets the cooldown to which the key specified if the cooldown is not active.
     *
     * @param cooldownKey the key to which cooldown to be reset (i.e. to active the cooldown)
     * @return true if the cooldown to which the key specified is not active, otherwise not
     */
    boolean test(UUID cooldownKey) {
        return cooldownMap.test(cooldownKey);
    }

    /**
     * Resets cooldown.
     *
     * @param cooldownKey the cooldown to which the key specified to be reset
     */
    void reset(UUID cooldownKey) {
        cooldownMap.reset(cooldownKey);
    }

    /**
     * Checks if the specific player has sufficient balance. If the balance is
     * NOT sufficient, it would send relevant messages to the player. Messages
     * will not be sent if the player has sufficient balance.
     *
     * @param player The player who is to be charged.
     * @param cost   How much we charge the player.
     * @return True if the player has sufficient balance. False else wise.
     */
    boolean checkBalance(Player player, int cost) {
        if (MoeUtils.economy().has(player, cost))
            return true;
        player.sendMessage(MoeUtils.text("common.not_enough_money"));
        return false;
    }

    /**
     * Attempts to charge player with specific fee. If the charging is successful, it
     * will send relevant messages to the player.
     *
     * @param player The player who is to be charged.
     * @param cost   The amount of fee applied to the specific player.
     */
    void chargePlayer(Player player, int cost) {
        MoeUtils.economy().withdrawPlayer(player, cost);
        MoeCore.plugin.systemAccount().depositSystem(cost);
        player.sendMessage(MoeUtils.text("common.price", "cost", cost));
    }

}
