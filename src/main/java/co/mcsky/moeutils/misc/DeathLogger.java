package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.util.DamageCauseLocalization;
import com.meowj.langutils.lang.LanguageHelper;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class DeathLogger implements TerminableModule {

    private final static String separator = ", ";
    private final Set<EntityType> hashLoggedCreatures;

    public DeathLogger() {
        hashLoggedCreatures = new HashSet<>();
        hashLoggedCreatures.addAll(plugin.config.logged_creatures);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("DeathLogger", plugin.config.death_logger_enabled)) return;

        Events.subscribe(EntityDeathEvent.class)
                .filter(e -> hashLoggedCreatures.contains(e.getEntityType()))
                .handler(e -> {
                    LivingEntity entity = e.getEntity();
                    String victimName = entity.getCustomName() != null
                            ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), MoeConfig.DEFAULT_LANG) + ")"
                            : LanguageHelper.getEntityName(e.getEntityType(), MoeConfig.DEFAULT_LANG);

                    if (entity.getLastDamageCause() == null) return; // to avoid potential NPE
                    String damageCause = DamageCauseLocalization.valueOf(entity.getLastDamageCause().getCause().name()).getLocalization();

                    // get the player relevant to this death.
                    Player killer = entity.getKiller();
                    String killerName;
                    if (killer != null) {
                        killerName = killer.getName();
                    } else {
                        List<Player> nearbyPlayers = new ArrayList<>(e.getEntity().getLocation().getNearbyPlayers(plugin.config.search_radius));
                        if (nearbyPlayers.size() != 0) {
                            // All nearby players are included.
                            killerName = nearbyPlayers.stream()
                                    .map(HumanEntity::getName)
                                    .reduce((acc, name) -> acc + separator + name)
                                    .get();
                        } else {
                            killerName = plugin.getMessage(null, "common.none");
                        }
                    }

                    String location = entity.getLocation().getWorld().getName() + separator +
                            entity.getLocation().getBlockX() + separator +
                            entity.getLocation().getBlockY() + separator +
                            entity.getLocation().getBlockZ();

                    plugin.getServer().broadcastMessage(plugin.getMessage(killer, "death-logger.death",
                            "victim", victimName,
                            "reason", damageCause,
                            "killer", killerName,
                            "location", location));

                }).bindWith(consumer);
    }
}
