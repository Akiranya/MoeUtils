package co.mcsky.reminder;

import co.mcsky.MoeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class CreatureDeathMessage implements Listener {
    private final MoeUtils moe;
    private final Set<EntityType> whitelist;

    public CreatureDeathMessage(MoeUtils moe) {
        this.moe = moe;
        this.whitelist = moe.setting.reminder.whitelist;

        // 判断是否要注册 Listener
        if (moe.setting.reminder.enable) {
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

            // 如果 victim 有自定义名字（例如命名牌），则显示自定义名字
            String victimName = livingEntity.getCustomName() != null ?
                    livingEntity.getCustomName() :
                    livingEntity.getName();

            Location loc = livingEntity.getLocation();
            Player killer = livingEntity.getKiller();
            String playerName = killer != null ? killer.getName() : moe.setting.globe.msg_none;
            String locSb = loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();

            String serverMsg = String.format(moe.setting.reminder.msg_death, victimName, deathCause, playerName, locSb);
            moe.getServer().broadcastMessage(serverMsg);
        });
    }

}
