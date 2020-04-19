package co.mcsky.mobarena;

import co.mcsky.MoeUtils;
import co.mcsky.utilities.NameTagUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static co.mcsky.utilities.NameTagUtil.ACTION.UPDATE;

public class PlayerHealthListener implements Listener {
    private final NameTagUtil nameTagUtil;

    PlayerHealthListener(MoeUtils moe, NameTagUtil nameTagUtil) {
        // Pass the CustomPlayerName instance instead of initializing a new one
        this.nameTagUtil = nameTagUtil;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !nameTagUtil.getPlayers().contains(victim))
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
        if (!(entity instanceof Player) || !nameTagUtil.getPlayers().contains(entity))
            return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            nameTagUtil.change(p, nameTagUtil.color("&a&l[&r"), nameTagUtil.color("&a&l]"), UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            nameTagUtil.change(p, nameTagUtil.color("&e&l[&r"), nameTagUtil.color("&e&l]"), UPDATE);
        } else {
            nameTagUtil.change(p, nameTagUtil.color("&c&l[&r"), nameTagUtil.color("&c&l]"), UPDATE);
        }
    }
}
