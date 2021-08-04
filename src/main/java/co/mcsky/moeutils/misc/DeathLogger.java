package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeConfig;
import co.mcsky.moeutils.MoeUtils;
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

public class DeathLogger implements TerminableModule {

    private final static String separator = ", ";
    private final Set<EntityType> hashLoggedCreatures;

    public DeathLogger() {
        hashLoggedCreatures = new HashSet<>();
        hashLoggedCreatures.addAll(MoeUtils.plugin.config.logged_creatures);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("DeathLogger", MoeUtils.plugin.config.death_logger_enabled)) return;

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
                        List<Player> nearbyPlayers = new ArrayList<>(e.getEntity().getLocation().getNearbyPlayers(MoeUtils.plugin.config.search_radius));
                        if (nearbyPlayers.size() != 0) {
                            // All nearby players are included.
                            killerName = nearbyPlayers.stream()
                                    .map(HumanEntity::getName)
                                    .reduce((acc, name) -> acc + separator + name)
                                    .get();
                        } else {
                            killerName = MoeUtils.plugin.getMessage(null, "common.none");
                        }
                    }

                    String location = entity.getLocation().getWorld().getName() + separator +
                            entity.getLocation().getBlockX() + separator +
                            entity.getLocation().getBlockY() + separator +
                            entity.getLocation().getBlockZ();

                    MoeUtils.plugin.getServer().broadcastMessage(MoeUtils.plugin.getMessage(killer, "death-logger.death",
                            "victim", victimName,
                            "reason", damageCause,
                            "killer", killerName,
                            "location", location));

                }).bindWith(consumer);
    }

    /**
     * Contains chinese localization for all damage causes.
     */
    @SuppressWarnings("unused")
    public enum DamageCauseLocalization {

        BLOCK_EXPLOSION("方块爆炸"),
        CONTACT("触碰"),
        CRAMMING("拥挤"),
        CUSTOM("自定义"),
        DRAGON_BREATH("龙息"),
        DROWNING("淹死"),
        DRYOUT("干渴"),
        ENTITY_ATTACK("生物攻击"),
        ENTITY_EXPLOSION("生物爆炸"),
        ENTITY_SWEEP_ATTACK("生物横扫攻击"),
        FALL("跌落"),
        FALLING_BLOCK("被方块压死"),
        FIRE("火烧"),
        FIRE_TICK("火烧"),
        FLY_INTO_WALL("撞墙"),
        HOT_FLOOR("高温地板"),
        LAVA("岩浆"),
        LIGHTNING("闪电"),
        MAGIC("魔法"),
        MELTING("融化"),
        POISON("中毒"),
        PROJECTILE("弹射物"),
        STARVATION("饥饿"),
        SUFFOCATION("窒息"),
        SUICIDE("自杀"),
        THORNS("荆棘"),
        VOID("虚空"),
        WITHER("凋零");

        private final String localization;

        DamageCauseLocalization(String localization) {
            this.localization = localization;
        }

        public String getLocalization() {
            return this.localization;
        }

    }
}
