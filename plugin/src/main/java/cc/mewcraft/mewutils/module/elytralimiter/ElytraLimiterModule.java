package cc.mewcraft.mewutils.module.elytralimiter;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewcore.progressbar.ProgressbarMessenger;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@DefaultQualifier(NonNull.class)
public class ElytraLimiterModule extends ModuleBase {

    private final Set<String> restrictedWorlds;
    private final Set<BoostMethod> restrictedBoost;
    private final ProgressbarMessenger progressbarMessenger;
    private final ChargeBasedCooldownMap<UUID> cooldownMap;

    @Inject
    public ElytraLimiterModule(MewPlugin plugin) {
        super(plugin);

        this.restrictedBoost = EnumSet.copyOf(MewUtils.config().slow_elytra_methods.stream().map(BoostMethod::valueOf).toList());
        this.restrictedWorlds = new HashSet<>(MewUtils.config().slow_elytra_worlds);
        this.progressbarMessenger = new ProgressbarMessenger(MewUtils.config().slow_elytra_bar_stay_time,
            ProgressbarGenerator.Builder.builder()
                .left(MewUtils.translations().of("slow_elytra.cooldown-progressbar.left").plain())
                .full(MewUtils.translations().of("slow_elytra.cooldown-progressbar.full").plain())
                .empty(MewUtils.translations().of("slow_elytra.cooldown-progressbar.empty").plain())
                .right(MewUtils.translations().of("slow_elytra.cooldown-progressbar.right").plain())
                .width(MewUtils.config().slow_elytra_bar_width)
                .build()
        );
        this.cooldownMap = ChargeBasedCooldownMap.create(
            Cooldown.of(MewUtils.config().slow_elytra_cooldown, TimeUnit.MILLISECONDS),
            uuid -> MewUtils.config().slow_elytra_cooldown_charge
        );
    }

    @Override protected void enable() {
        registerListener(new ElytraBoostListener(this));
    }

    public ProgressbarMessenger getProgressbarMessenger() {
        return this.progressbarMessenger;
    }

    public ChargeBasedCooldownMap<UUID> getCooldownMap() {
        return this.cooldownMap;
    }

    public boolean isBoostAllowed(BoostMethod method) {
        return !this.restrictedBoost.contains(method);
    }

    public boolean inRestrictedWorld(Player player) {
        return this.restrictedWorlds.contains(player.getWorld().getName());
    }

    public boolean underTPSThreshold() {
        return MewUtils.INSTANCE.getServer().getTPS()[0] <= MewUtils.config().slow_elytra_tps_threshold;
    }

    @Override
    public String getId() {
        return "elytralimiter";
    }

    @Override
    public boolean canEnable() {
        return true;
    }

}
