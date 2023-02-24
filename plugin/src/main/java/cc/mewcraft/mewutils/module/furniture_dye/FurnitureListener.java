package cc.mewcraft.mewutils.module.furniture_dye;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FurnitureListener implements AutoCloseableListener {

    private final FurnitureDyeModule module;

    public FurnitureListener(final FurnitureDyeModule module) {
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
