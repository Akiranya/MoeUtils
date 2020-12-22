package co.mcsky.moeutils.mobarena;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class ProjectileCollideListener implements Listener {

    private final MobArenaAddon mobArenaAddon;

    ProjectileCollideListener(MobArenaAddon mobArenaAddon) {
        this.mobArenaAddon = mobArenaAddon;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onProjectileCollide(ProjectileCollideEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        // Ignore if projectiles are not from players
        if (!(shooter instanceof Player))
            return;

        // Ignore if player is not in arena
        if (!mobArenaAddon.getMobArena().getArenaMaster().getAllLivingPlayers().contains(shooter))
            return;

        if (MobArenaAddon.whiteList.contains(event.getCollidedWith().getType())) {
            event.setCancelled(true);
        }
    }

}
