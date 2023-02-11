package cc.mewcraft.mewutils.module.furnituredyer;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;

public class FurnitureModule extends ModuleBase {

    private final FurnitureDyeHandler dyeHandler;

    @Inject
    public FurnitureModule(final MewPlugin plugin) {
        super(plugin);

        this.dyeHandler = new FurnitureDyeHandler(plugin);
    }

    @Override protected void enable() {
        registerListener(new FurnitureListener(this));
    }

    public FurnitureDyeHandler getDyeHandler() {
        return this.dyeHandler;
    }

    @Override public String getId() {
        return "furnituredye";
    }

    @Override public boolean canEnable() {
        return HookChecker.hasItemsAdder();
    }

}
