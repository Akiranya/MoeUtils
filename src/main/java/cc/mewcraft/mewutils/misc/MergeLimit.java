package cc.mewcraft.mewutils.misc;

import cc.mewcraft.mewutils.MewUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.event.entity.ItemMergeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class MergeLimit implements TerminableModule {

    private final EnumSet<Material> types;

    public MergeLimit() {
        this.types = EnumSet.copyOf(MewUtils.config().merge_limit_types);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        if (MewUtils.logStatus("MergeLimit", MewUtils.config().merge_limit_enabled)) {
            return;
        }

        Events.subscribe(ItemMergeEvent.class).handler(event -> {
            if (types.contains(event.getEntity().getItemStack().getType())) {
                if (event.getEntity().getItemStack().getAmount() > MewUtils.config().merge_limit_threshold ||
                    event.getTarget().getItemStack().getAmount() > MewUtils.config().merge_limit_threshold) {
                    event.getEntity().remove();
                    event.getTarget().remove();
                }
            }
        }).bindWith(consumer);
    }
}
