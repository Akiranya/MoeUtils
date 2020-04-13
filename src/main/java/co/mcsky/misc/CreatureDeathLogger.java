package co.mcsky.misc;

import co.mcsky.MoeUtils;
import co.mcsky.config.CreatureDeathLoggerConfig;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreatureDeathLogger implements Listener {
    private final MoeUtils moe;
    private final CreatureDeathLoggerConfig cfg;
    private final Set<EntityType> LoggedCreatures;

    public CreatureDeathLogger(MoeUtils moe) {
        this.moe = moe;
        this.cfg = moe.creatureDeathLoggerConfig;
        this.LoggedCreatures = cfg.getCreatures();
        if (cfg.isEnable()) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("CreatureDeathLogger is enabled");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!LoggedCreatures.contains(entity.getType())) return;
        String victimName = entity.getCustomName() != null
                ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), moe.commonConfig.lang) + ")"
                : LanguageHelper.getEntityName(e.getEntityType(), moe.commonConfig.lang);
        @SuppressWarnings("ConstantConditions")
        String cause = checkNotNull(e.getEntity().getLastDamageCause().getCause()).name();
        String player = entity.getKiller() != null
                ? entity.getKiller().getName()
                : moe.commonConfig.msg_none;
        String separator = ", ";
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder loc = new StringBuilder()
                .append(entity.getLocation().getWorld().getName())
                .append(separator)
                .append(entity.getLocation().getBlockX())
                .append(separator)
                .append(entity.getLocation().getBlockY())
                .append(separator)
                .append(entity.getLocation().getBlockZ());
        moe.getServer().broadcastMessage(String.format(cfg.msg_death, victimName, cause, player, loc.toString()));
    }

}
