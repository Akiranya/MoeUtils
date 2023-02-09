package cc.mewcraft.mewutils.furniture;

import cc.mewcraft.mewutils.MewUtils;
import com.google.inject.Inject;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class FurnitureModule implements TerminableModule {

    private final MewUtils plugin;
    private final FurnitureDyeHandler dyeHandler;

    @Inject
    public FurnitureModule(final MewUtils plugin, final FurnitureDyeHandler dyeHandler) {
        this.plugin = plugin;
        this.dyeHandler = dyeHandler;
    }

    @Override public void setup(@NotNull final TerminableConsumer consumer) {
        new FurnitureListener(plugin, dyeHandler).bindModuleWith(consumer);
    }

}
