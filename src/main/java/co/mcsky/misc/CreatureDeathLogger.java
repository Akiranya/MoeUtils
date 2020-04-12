package co.mcsky.misc;

import co.mcsky.MoeUtils;
import co.mcsky.config.CreatureDeathLoggerConfig;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Set;

public class CreatureDeathLogger implements Listener {
    private final MoeUtils moe;
    private final CreatureDeathLoggerConfig cfg;
    private final Set<EntityType> whitelist;

    public CreatureDeathLogger(MoeUtils moe) {
        this.moe = moe;
        this.cfg = moe.creatureDeathLoggerConfig;
        this.whitelist = cfg.getCreatures();
        if (cfg.isEnable()) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!whitelist.contains(entity.getType())) return;
        String victimName = entity.getCustomName() != null ? entity.getCustomName() : entity.getName(); // 显示命名
        String cause = null;
        if (e.getEntity().getLastDamageCause() != null) {
            cause = e.getEntity().getLastDamageCause().getCause().name();
        }
        String player = entity.getKiller() != null ? entity.getKiller().getName() : moe.commonConfig.msg_none;
        Location location = entity.getLocation();
        String locationFormat = location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
        String serverMsg = String.format(cfg.msg_death, victimName, cause, player, locationFormat);
        moe.getServer().broadcastMessage(serverMsg);
    }

}
