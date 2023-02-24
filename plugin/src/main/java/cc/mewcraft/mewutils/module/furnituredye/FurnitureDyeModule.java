package cc.mewcraft.mewutils.module.furnituredye;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;

public class FurnitureDyeModule extends ModuleBase {

    private final FurnitureDyeHandler dyeHandler;

    @Inject
    public FurnitureDyeModule(final MewPlugin plugin) {
        super(plugin);

        this.dyeHandler = new FurnitureDyeHandler(plugin);
    }

    @Override protected void enable() {
        registerListener(new FurnitureListener(this));
    }

    public FurnitureDyeHandler getDyeHandler() {
        return this.dyeHandler;
    }

    @Override public boolean checkRequirement() {
        return HookChecker.hasItemsAdder();
    }

}
