package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.config.Config;
import co.mcsky.moeutils.utilities.DamageCauseLocalization;
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

    public final MoeUtils plugin;
    public final Config config;

    private final Set<EntityType> loggedCreatures;
    private final String separator = ", ";

    public DeathLogger(MoeUtils plugin) {
        this.plugin = plugin;
        this.config = plugin.config;
        this.loggedCreatures = config.DEATHLOGGER_CREATURES;
        if (config.DEATHLOGGER_ENABLE) {
            this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
            this.plugin.getLogger().info("DeathLogger is enabled.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!loggedCreatures.contains(entity.getType()))
            return;

        String lang_setting = plugin.getMessage(null, "common.lang");
        String victimName = entity.getCustomName() != null
                            ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), lang_setting) + ")"
                            : LanguageHelper.getEntityName(e.getEntityType(), lang_setting);

        @SuppressWarnings("ConstantConditions")
        String damageCause = DamageCauseLocalization.valueOf(e.getEntity().getLastDamageCause().getCause().name()).getLocalization();

        // Get the player relevant to this death.
        Player killer = entity.getKiller();
        String killerName;
        if (killer != null) {
            killerName = killer.getName();
        } else {
            List<Player> nearbyPlayers = new ArrayList<>(e.getEntity().getLocation().getNearbyPlayers(config.DEATHLOGGER_SEARCH_RADIUS));
            if (nearbyPlayers.size() != 0) {
                // All nearby players are included.
                killerName = nearbyPlayers.stream()
                                          .map(HumanEntity::getName)
                                          .reduce((acc, name) -> acc + separator + name)
                                          .get();
            } else {
                killerName = plugin.getMessage(killer, "common.none");
            }
        }

        String location = entity.getLocation().getWorld().getName() +
                          separator +
                          entity.getLocation().getBlockX() +
                          separator +
                          entity.getLocation().getBlockY() +
                          separator +
                          entity.getLocation().getBlockZ();

        plugin.getServer().broadcastMessage(String.format(plugin.getMessage(killer, "deathlogger.entity_death"),
                                                          victimName, damageCause, killerName, location));
    }

}
