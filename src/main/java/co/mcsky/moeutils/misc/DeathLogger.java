package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.Configuration;
import co.mcsky.moeutils.utilities.DamageCauseLocalization;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class DeathLogger implements Listener {

    private final static String separator = ", ";

    public static boolean enable;
    public static int searchRadius;
    public static Set<EntityType> loggedCreatures;

    public final Configuration config;

    @SuppressWarnings("SimplifyStreamApiCallChains")
    public DeathLogger() {
        config = plugin.config;

        // Configuration values
        enable = plugin.config.node("deathlogger", "enable").getBoolean();
        searchRadius = plugin.config.node("deathlogger", "searchRadius").getInt(32);
        try {
            loggedCreatures = plugin.config.node("deathlogger", "creatures").getList(EntityType.class, List.of(EntityType.VILLAGER)).stream().collect(Collectors.toSet());
        } catch (final SerializationException e) {
            plugin.getLogger().severe("DeathLogger initialization failed! Please validate the configuration.");
            return;
        }

        // Register this listener
        if (enable) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("DeathLogger is enabled.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!loggedCreatures.contains(entity.getType()))
            return;

        String default_lang = plugin.config.getLanguage();
        String victimName = entity.getCustomName() != null
                            ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), default_lang) + ")"
                            : LanguageHelper.getEntityName(e.getEntityType(), default_lang);

        @SuppressWarnings("ConstantConditions")
        String damageCause = DamageCauseLocalization.valueOf(e.getEntity().getLastDamageCause().getCause().name()).getLocalization();

        // Get the player relevant to this death.
        Player killer = entity.getKiller();
        String killerName;
        if (killer != null) {
            killerName = killer.getName();
        } else {
            List<Player> nearbyPlayers = new ArrayList<>(e.getEntity().getLocation().getNearbyPlayers(searchRadius));
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

        plugin.getServer().broadcastMessage(plugin.getMessage(killer, "deathlogger.death",
                                                              "victim", victimName,
                                                              "reason", damageCause,
                                                              "killer", killerName,
                                                              "location", location));
    }

}
