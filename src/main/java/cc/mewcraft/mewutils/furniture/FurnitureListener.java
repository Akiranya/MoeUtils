package cc.mewcraft.mewutils.furniture;

import cc.mewcraft.mewutils.MewUtils;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FurnitureListener implements TerminableModule {

    private final MewUtils plugin;
    private final FurnitureDyeHandler handler;

    public FurnitureListener(final MewUtils plugin) {
        this.plugin = plugin;
        this.handler = new FurnitureDyeHandler(plugin);
    }

    @Override public void setup(@NotNull final TerminableConsumer consumer) {
        Events.subscribe(FurnitureInteractEvent.class).handler(this::onInteractFurniture).bindWith(consumer);
    }

    public void onInteractFurniture(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking())
            handler.dye(player, event.getBukkitEntity());
    }

}