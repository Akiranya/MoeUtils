package co.mcsky.mobarena;

import co.mcsky.MoeUtils;
import co.mcsky.utilities.NametagUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static co.mcsky.utilities.NametagUtil.ACTION.UPDATE;

public class PlayerHealthListener implements Listener {

    private final NametagUtil nametagUtil;

    PlayerHealthListener(MoeUtils moe, NametagUtil nametagUtil) {
        this.nametagUtil = nametagUtil;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !nametagUtil.getPlayers().contains(victim))
            return;
        updateTag((Player) victim);
    }

    /**
     * Update player's health when healed
     */
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(entity instanceof Player) || !nametagUtil.getPlayers().contains(entity))
            return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            nametagUtil.change(p, nametagUtil.color("&a&l[&r"), nametagUtil.color("&a&l]"), UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            nametagUtil.change(p, nametagUtil.color("&e&l[&r"), nametagUtil.color("&e&l]"), UPDATE);
        } else {
            nametagUtil.change(p, nametagUtil.color("&c&l[&r"), nametagUtil.color("&c&l]"), UPDATE);
        }
    }

}
