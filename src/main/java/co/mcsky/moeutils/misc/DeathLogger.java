package co.mcsky.moeutils.misc;

import co.aikar.commands.ACFBukkitUtil;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

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
        if (MoeUtils.logActiveStatus("DeathLogger", MoeUtils.plugin.config.death_logger_enabled))
            return;

        Events.subscribe(EntityDeathEvent.class)
                .filter(e -> hashLoggedCreatures.contains(e.getEntityType()))
                .handler(e -> {
                    LivingEntity entity = e.getEntity();
                    String victimName = entity.getCustomName() != null
                            ? entity.getCustomName() + "(" + LanguageHelper.getEntityName(e.getEntityType(), MoeConfig.DEFAULT_LANG) + ")"
                            : LanguageHelper.getEntityName(e.getEntityType(), MoeConfig.DEFAULT_LANG);

                    if (entity.getLastDamageCause() == null)
                        return; // to avoid potential NPE
                    String damageCause = getLocalization(entity.getLastDamageCause().getCause());

                    // get the player relevant to this death.
                    Player killer = entity.getKiller();
                    String killerName;
                    if (killer != null) {
                        killerName = killer.getName();
                    } else {
                        List<Player> nearbyPlayers = e.getEntity().getLocation().getNearbyPlayers(MoeUtils.plugin.config.search_radius).stream().toList();
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

                    String location = ACFBukkitUtil.blockLocationToString(entity.getLocation());
                    MoeUtils.plugin.getServer().broadcastMessage(MoeUtils.plugin.getMessage(killer, "death-logger.death",
                            "victim", victimName,
                            "reason", damageCause,
                            "killer", killerName,
                            "location", location));

                }).bindWith(consumer);
    }

    private String getLocalization(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case FALL -> "跌落";
            case FIRE, FIRE_TICK -> "火烧";
            case LAVA -> "岩浆";
            case VOID -> "虚空";
            case MAGIC -> "魔法";
            case CUSTOM -> "自定义";
            case DRYOUT -> "干渴";
            case FREEZE -> "冰冻";
            case POISON -> "中毒";
            case THORNS -> "荆棘";
            case WITHER -> "凋零";
            case CONTACT -> "接触";
            case MELTING -> "融化";
            case SUICIDE -> "自杀";
            case CRAMMING -> "拥挤";
            case DROWNING -> "淹死";
            case HOT_FLOOR -> "高温地板";
            case LIGHTNING -> "闪电";
            case ENTITY_ATTACK -> "生物攻击";
            case ENTITY_SWEEP_ATTACK -> "生物横扫攻击";
            case PROJECTILE -> "弹射物";
            case STARVATION -> "饥饿";
            case SUFFOCATION -> "窒息";
            case BLOCK_EXPLOSION -> "方块爆炸";
            case ENTITY_EXPLOSION -> "实体爆炸";
            case FALLING_BLOCK -> "跌落方块";
            case DRAGON_BREATH -> "龙息";
            case FLY_INTO_WALL -> "卡进墙里";
        };
    }

}
