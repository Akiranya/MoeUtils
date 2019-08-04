package co.mcsky.notifier;

import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class VillagerDeathAnnouncer implements Listener {
    final private MoeUtils moe;
    private Set<EntityType> whitelist;

    public VillagerDeathAnnouncer(MoeUtils moe) {
        this.moe = moe;
        whitelist = moe.config.notifier_whitelist;
        if (moe.config.notifier_on) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return; // Only handle living entity.
        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        // This is really important to check isDead() in the next minecraft tick.
        Bukkit.getScheduler().runTask(moe, () -> {
            if (!livingEntity.isDead()) return; // Ignore if the entity is still alive
            if (!whitelist.contains(livingEntity.getType())) return; // Ignore if it is not in whitelist
            String deathCause = event.getCause().name();
            String customName = livingEntity.getCustomName();
            String entityName = customName != null ? customName : livingEntity.getName();
            Location loc = livingEntity.getLocation();
            StringBuilder locSb = new StringBuilder();
            locSb.append(loc.getBlockX());
            locSb.append(", ");
            locSb.append(loc.getBlockY());
            locSb.append(", ");
            locSb.append(loc.getBlockZ());
            String format = String.format(moe.config.notifier_message_death, entityName, deathCause, locSb.toString());
            moe.getServer().broadcastMessage(format);
        });
    }

}
