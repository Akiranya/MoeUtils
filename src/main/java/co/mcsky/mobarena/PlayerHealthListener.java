package co.mcsky.mobarena;

import co.mcsky.CustomPlayerName;
import co.mcsky.MoeUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static co.mcsky.CustomPlayerName.ACTION.UPDATE;

public class PlayerHealthListener implements Listener {
    private CustomPlayerName cpn;

    public PlayerHealthListener(MoeUtils plugin, CustomPlayerName custom) {
        // Pass the CustomPlayerName instance instead of initializing a new one
        this.cpn = custom;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !cpn.getPlayers().contains(victim)) return;
        updateTag((Player) victim);
    }

    /**
     * Update player's health when healed
     */
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(entity instanceof Player) || !cpn.getPlayers().contains(entity)) return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            cpn.change(p, cpn.color("&a&l[&r"), cpn.color("&a&l]"), UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            cpn.change(p, cpn.color("&e&l[&r"), cpn.color("&e&l]"), UPDATE);
        } else {
            cpn.change(p, cpn.color("&c&l[&r"), cpn.color("&c&l]"), UPDATE);
        }
    }
}
