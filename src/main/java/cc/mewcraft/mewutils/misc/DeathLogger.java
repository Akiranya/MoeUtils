package cc.mewcraft.mewutils.misc;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewcore.text.Text;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class DeathLogger implements TerminableModule {

    private final Set<EntityType> hashLoggedCreatures;

    public DeathLogger() {
        hashLoggedCreatures = new HashSet<>();
        hashLoggedCreatures.addAll(MewUtils.config().logged_creatures);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MewUtils.logStatus("DeathLogger", MewUtils.config().death_logger_enabled))
            return;

        Events.subscribe(EntityDeathEvent.class)
                .filter(e -> hashLoggedCreatures.contains(e.getEntityType()))
                .handler(e -> {
                    LivingEntity entity = e.getEntity();
                    if (entity.getLastDamageCause() == null) return;
                    Player killer = entity.getKiller();
                    String killerName;
                    if (killer != null) {
                        killerName = killer.getName();
                    } else {
                        // search for nearby players
                        killerName = entity.getLocation().getNearbyPlayers(MewUtils.config().search_radius)
                                .stream()
                                .map(Player::getName)
                                .reduce((acc, name) -> acc.concat(",").concat(name))
                                .orElse(MewUtils.text("common.none"));
                    }
                    Component victimName;
                    if (entity.customName() != null) {
                        // add custom name if there is one
                        victimName = MiniMessage.miniMessage().deserialize("<lang:%s>({customName})".formatted(entity.getType().translationKey()))
                                .replaceText(b -> b.matchLiteral("{customName}").replacement(entity.customName()))
                                .hoverEvent(entity.asHoverEvent());
                    } else {
                        victimName = entity.name();
                    }
                    MewUtils.text3("death-logger.death")
                            .replace("victim", victimName)
                            .replace("reason", getLocalization(entity.getLastDamageCause().getCause()))
                            .replace("killer", killerName)
                            .replace("x", entity.getLocation().getBlockX())
                            .replace("y", entity.getLocation().getBlockY())
                            .replace("z", entity.getLocation().getBlockZ())
                            .replace("world", entity.getLocation().getWorld().getName())
                            .broadcast(Text.MessageType.CHAT);
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
            case SONIC_BOOM -> "音爆";
        };
    }

}
