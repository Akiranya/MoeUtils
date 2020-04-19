package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.config.CommonConfig;
import co.mcsky.utilities.CooldownUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

import static co.mcsky.MoeUtils.economy;

/**
 * This class shares code for {@link MagicTime} and {@link MagicWeather}.
 */
public class MagicBase {

    public final int COOLDOWN_DURATION;
    public final MoeUtils moe;
    public final CommonConfig cfg;

    public MagicBase(MoeUtils moe, int cooldownDuration) {
        this.moe = moe;
        this.cfg = moe.commonConfig;
        this.COOLDOWN_DURATION = cooldownDuration;
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
    public boolean checkCooldown(Player player, UUID COOLDOWN_KEY) {
        if (CooldownUtil.check(COOLDOWN_KEY, COOLDOWN_DURATION)) return true;
        String playerMsg = String.format(cfg.msg_cooldown, CooldownUtil.remaining(COOLDOWN_KEY, COOLDOWN_DURATION));
        player.sendMessage(playerMsg);
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
    public boolean checkBalance(Player player, int cost) {
        if (economy.has(player, cost)) return true;
        player.sendMessage(cfg.msg_not_enough_money);
        return false;
    }

    /**
     * Try to charge player with specific fee. If the charging is successful, it
     * will send relevant messages to the player.
     *
     * @param player The player who is to be charged.
     * @param cost   The amount of fee applied to the specific player.
     */
    public void chargePlayer(Player player, int cost) {
        economy.withdrawPlayer(player, cost);
        String playerMsg = String.format(cfg.msg_cost, cost);
        player.sendMessage(playerMsg);
    }

}
