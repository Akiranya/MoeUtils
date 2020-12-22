package co.mcsky.moeutils.mobarena;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.utilities.NametagManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerHealthListener implements Listener {

    private final NametagManager ntManager;

    PlayerHealthListener(MoeUtils moe, NametagManager ntManager) {
        this.ntManager = ntManager;
        moe.getServer().getPluginManager().registerEvents(this, moe);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !ntManager.getPlayers().contains(victim))
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
        if (!(entity instanceof Player) || !ntManager.getPlayers().contains(entity))
            return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            ntManager.change(p, ntManager.color("&a&l[&r"), ntManager.color("&a&l]"), NametagManager.ACTION.UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            ntManager.change(p, ntManager.color("&e&l[&r"), ntManager.color("&e&l]"), NametagManager.ACTION.UPDATE);
        } else {
            ntManager.change(p, ntManager.color("&c&l[&r"), ntManager.color("&c&l]"), NametagManager.ACTION.UPDATE);
        }
    }

}
