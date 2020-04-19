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

    private final MobArena ma;
    private final Set<EntityType> whiteList;

    ProjectileCollideListener(MoeUtils moe, MobArena ma) {
        this.ma = ma;
        this.whiteList = moe.mobArenaProCfg.whitelist;
        moe.getServer().getPluginManager().registerEvents(this, moe);
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
