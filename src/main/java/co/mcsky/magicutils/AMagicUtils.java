package co.mcsky.magicutils;

import co.mcsky.MoeUtils;
import co.mcsky.util.CooldownUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static co.mcsky.MoeUtils.economy;

/**
 * 由于 class MagicTime 和 MagicWeather 共享很多一样的方法，
 * 所以创建了一个 abstract class AMagicUtils 来减少重复的代码。
 */
abstract class AMagicUtils<K> extends CooldownUtil<K> {

    final int COOLDOWN_LENGTH;
    public final MoeUtils moe;

    AMagicUtils(MoeUtils moe, int cooldown_length) {
        this.moe = moe;
        this.COOLDOWN_LENGTH = cooldown_length;
    }

    /**
     * @return 返回 True 如果冷却没有就绪
     */
    boolean checkCooldown(Player player, K COOLDOWN_KEY) {
        if (check(COOLDOWN_KEY, COOLDOWN_LENGTH)) return false; // 冷却好了就直接返回

        String playerMsg = String.format(moe.setting.globe.msg_cooldown,
                remaining(COOLDOWN_KEY, COOLDOWN_LENGTH));
        player.sendMessage(playerMsg);
        return true;
    }

    /**
     * @return 返回 True 如果玩家没有足够的钱
     */
    boolean checkBalance(Player player, int cost) {
        if (economy.has(player, cost)) return false; // 如果软妹币足够就直接返回
        player.sendMessage(moe.setting.globe.msg_not_enough_money);
        return true;
    }

    /**
     * 调用了 heh 插件的「系统扣费指令」来收取费用 xD
     * 这样可以将消费额转到系统余额，让资金回流到玩家手里
     */
    void charge(Player player, int cost) {
        String cmd = String.format("hamsterecohelper:heh balance take %s %d", player.getName(), cost);
        CommandSender console = moe.getServer().getConsoleSender();
        moe.getServer().dispatchCommand(console, cmd); // This command CHARGES player
        // 因为调用 heh 的扣费指令就自带玩家提示信息，所以不再需要多加提示
//        String playerMsg = String.format(moe.setting.global_message_cost, cost);
//        player.sendMessage(playerMsg);
    }

}
