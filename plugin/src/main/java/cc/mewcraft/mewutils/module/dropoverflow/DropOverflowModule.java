package cc.mewcraft.mewutils.module.dropoverflow;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemMergeEvent;

import java.util.EnumSet;

public class DropOverflowModule extends ModuleBase implements AutoCloseableListener {

    private final EnumSet<Material> types;

    @Inject
    public DropOverflowModule(MewPlugin plugin) {
        super(plugin);

        this.types = EnumSet.copyOf(MewUtils.config().merge_limit_types);
    }

    @Override protected void enable() {
        registerListener(this);
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        if (this.types.contains(event.getEntity().getItemStack().getType())) {
            if (event.getEntity().getItemStack().getAmount() > MewUtils.config().merge_limit_threshold ||
                event.getTarget().getItemStack().getAmount() > MewUtils.config().merge_limit_threshold) {
                event.getEntity().remove();
                event.getTarget().remove();
            }
        }
    }

    @Override public String getId() {
        return "dropoverflow";
    }

    @Override public boolean canEnable() {
        return true;
    }

}
