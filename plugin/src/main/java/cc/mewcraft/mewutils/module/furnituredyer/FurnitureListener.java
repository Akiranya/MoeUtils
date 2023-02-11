package cc.mewcraft.mewutils.module.furnituredyer;

import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FurnitureListener implements AutoCloseableListener {

    private final FurnitureModule module;

    public FurnitureListener(final FurnitureModule module) {
        this.module = module;
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            this.module.getDyeHandler().dye(player, event.getBukkitEntity());
        }
    }

}
