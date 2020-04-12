package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.config.CommonConfig;
import co.mcsky.utilities.CooldownUtil;
import org.bukkit.entity.Player;

import static co.mcsky.MoeUtils.economy;

/**
 * This class shares code for {@link MagicTime} and {@link MagicWeather}.
 */
abstract class AMagicCommon<K> extends CooldownUtil<K> {

    final int COOLDOWN_LENGTH;
    final MoeUtils moe;
    private final CommonConfig cfg;

    AMagicCommon(MoeUtils moe, int cooldown_length) {
        this.moe = moe;
        this.cfg = moe.commonConfig;
        this.COOLDOWN_LENGTH = cooldown_length;
    }


    /**
     * @param player       The player you want to check cooldown.
     * @param COOLDOWN_KEY The cooldown key and this should be unique.
     * @return True if cooldown is not ready.
     */
    boolean checkCooldown(Player player, K COOLDOWN_KEY) {
        if (check(COOLDOWN_KEY, COOLDOWN_LENGTH)) return false; // 冷却好了就直接返回

        String playerMsg = String.format(cfg.msg_cooldown, remaining(COOLDOWN_KEY, COOLDOWN_LENGTH));
        player.sendMessage(playerMsg);
        return true;
    }

    /**
     * @param player The player who ought to be charged.
     * @param cost   How much we charge the player.
     * @return True if the player does not have enough money.
     */
    boolean checkBalance(Player player, int cost) {
        if (economy.has(player, cost)) return false; // 如果软妹币足够就直接返回
        player.sendMessage(cfg.msg_not_enough_money);
        return true;
    }

    void charge(Player player, int cost) {
        economy.withdrawPlayer(player, cost); // 执行扣钱操作
        String playerMsg = String.format(cfg.msg_cost, cost);
        player.sendMessage(playerMsg);
        // 由于 heh 的数据库设置始终存在问题，改用 Vault 向玩家扣费
        /*
        String cmd = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        CommandSender console = moe.getServer().getConsoleSender();
        moe.getServer().dispatchCommand(console, cmd); // This command CHARGES player
        */
    }

}
