package co.mcsky.moeutils.mobarena;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.garbagemule.MobArena.MobArena;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Set;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class ProjectileCollideListener implements Listener {

    private final MobArena ma;
    private final Set<EntityType> whiteList;

    ProjectileCollideListener(MobArena ma) {
        this.ma = ma;
        this.whiteList = plugin.config.mobarena_whitelist;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onProjectileCollide(ProjectileCollideEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        // Ignore if projectiles are not from players
        if (!(shooter instanceof Player)) return;
        // Ignore if player is not in arena
        if (!ma.getArenaMaster().getAllLivingPlayers().contains(shooter))
            return;
        if (whiteList.contains(event.getCollidedWith().getType())) {
            event.setCancelled(true);
        }
    }

}
