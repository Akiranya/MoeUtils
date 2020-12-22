package co.mcsky.moeutils.mobarena;

import co.mcsky.moeutils.utilities.NametagManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class PlayerHealthListener implements Listener {

    private final NametagManager nametagManager;

    PlayerHealthListener(NametagManager nametagManager) {
        this.nametagManager = nametagManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Update player's health when damaged
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        // 不影响竞技场外面的玩家
        if (!(victim instanceof Player) || !nametagManager.getPlayers().contains(victim))
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
        if (!(entity instanceof Player) || !nametagManager.getPlayers().contains(entity))
            return;
        updateTag((Player) entity);
    }

    private void updateTag(Player p) {
        if (p.getHealth() > 15) {
            nametagManager.change(p, nametagManager.color("&a&l[&r"), nametagManager.color("&a&l]"), NametagManager.ACTION.UPDATE);
        } else if (p.getHealth() > 10 && p.getHealth() <= 15) {
            nametagManager.change(p, nametagManager.color("&e&l[&r"), nametagManager.color("&e&l]"), NametagManager.ACTION.UPDATE);
        } else {
            nametagManager.change(p, nametagManager.color("&c&l[&r"), nametagManager.color("&c&l]"), NametagManager.ACTION.UPDATE);
        }
    }

}
