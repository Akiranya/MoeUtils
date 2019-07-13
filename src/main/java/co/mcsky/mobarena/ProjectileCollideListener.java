package co.mcsky.mobarena;

import co.mcsky.MoeUtils;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.garbagemule.MobArena.MobArena;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Set;

public class ProjectileCollideListener implements Listener {

    private MobArena mobArena;
    private Set<EntityType> whiteList;

    public ProjectileCollideListener(MoeUtils plugin, MobArena mobArena) {
        this.mobArena = mobArena;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        whiteList = plugin.getMoeConfig().whiteList;
    }

    @EventHandler
    public void onProjectileCollide(ProjectileCollideEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();

        // Ignores projectiles which are not from players
        if (!(shooter instanceof Player)) return;

        // Ignores if player is not in arena
        if (!mobArena.getArenaMaster().getAllLivingPlayers().contains(shooter)) return;

        if (whiteList.contains(event.getCollidedWith().getType())) {
            event.setCancelled(true);
        }
    }
}
