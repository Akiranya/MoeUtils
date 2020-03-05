package co.mcsky.reminder;

import co.mcsky.MoeUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!whitelist.contains(entity.getType())) return; // Ignore if it is not in whitelist
        // 如果 victim 有自定义名字（例如命名牌），则显示自定义名字
        String victimName = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
        String cause = null;
        if (e.getEntity().getLastDamageCause() != null) {
            cause = e.getEntity().getLastDamageCause().getCause().name();
        }
        String player = entity.getKiller() != null ? entity.getKiller().getName() : moe.setting.globe.msg_none;
        Location location = entity.getLocation();
        String locationFormat = location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
        String serverMsg = String.format(moe.setting.reminder.msg_death, victimName, cause, player, locationFormat);
        moe.getServer().broadcastMessage(serverMsg);
    }

}
