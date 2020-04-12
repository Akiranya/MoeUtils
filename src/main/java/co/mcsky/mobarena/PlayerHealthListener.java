package co.mcsky.mobarena;

import co.mcsky.MoeUtils;
import co.mcsky.utilities.TagUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static co.mcsky.utilities.TagUtil.ACTION.UPDATE;

public class PlayerHealthListener implements Listener {
    private final TagUtil th;

    PlayerHealthListener(MoeUtils moe, TagUtil th) {
        // Pass the CustomPlayerName instance instead of initializing a new one
        this.th = th;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !th.getPlayers().contains(victim)) return;
        updateTag((Player) victim);
    }

    /**
     * Update player's health when healed
     */
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(entity instanceof Player) || !th.getPlayers().contains(entity)) return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            th.change(p, th.color("&a&l[&r"), th.color("&a&l]"), UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            th.change(p, th.color("&e&l[&r"), th.color("&e&l]"), UPDATE);
        } else {
            th.change(p, th.color("&c&l[&r"), th.color("&c&l]"), UPDATE);
        }
    }
}
