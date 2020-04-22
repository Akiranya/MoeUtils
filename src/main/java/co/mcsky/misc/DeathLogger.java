package co.mcsky.misc;

import co.mcsky.config.Configuration;
import co.mcsky.LanguageRepository;
import co.mcsky.MoeUtils;
import co.mcsky.utilities.DamageCauseLocalization;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeathLogger implements Listener {

    private final MoeUtils moe;
    private final LanguageRepository lang;
    private final Configuration config;
    private final Set<EntityType> loggedCreatures;
    private final String separator = ", ";

    public DeathLogger(MoeUtils moe) {
        this.moe = moe;
        this.config = moe.config;
        this.lang = moe.lang;
        this.loggedCreatures = config.deathlogger_creatures;
        if (config.deathlogger_enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("CreatureDeathLogger is enabled.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!loggedCreatures.contains(entity.getType())) return;

        String victimName = entity.getCustomName() != null
                            ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), lang.common_lang) + ")"
                            : LanguageHelper.getEntityName(e.getEntityType(), lang.common_lang);

        @SuppressWarnings("ConstantConditions")
        String damageCause = DamageCauseLocalization.valueOf(e.getEntity().getLastDamageCause().getCause().name()).getLocalization();

        // Get the player relevant to this death.
        String playerName;
        if (entity.getKiller() != null) {
            playerName = entity.getKiller().getName();
        } else {
            List<Player> nearbyPlayers = new ArrayList<>(e.getEntity().getLocation().getNearbyPlayers(config.deathlogger_searchRadius));
            if (nearbyPlayers.size() != 0) {
                // All nearby players are included.
                playerName = nearbyPlayers.stream()
                                          .map(HumanEntity::getName)
                                          .reduce((acc, name) -> acc + separator + name)
                                          .get();
            } else {
                playerName = lang.common_none;
            }
        }

        String location = entity.getLocation().getWorld().getName() +
                          separator +
                          entity.getLocation().getBlockX() +
                          separator +
                          entity.getLocation().getBlockY() +
                          separator +
                          entity.getLocation().getBlockZ();

        moe.getServer().broadcastMessage(String.format(lang.deathlogger_entity_death, victimName, damageCause, playerName, location));
    }

}
