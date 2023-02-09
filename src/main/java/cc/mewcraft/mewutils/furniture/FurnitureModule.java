package cc.mewcraft.mewutils.furniture;

import cc.mewcraft.mewutils.MewUtils;

public class FurnitureModule {
    private final MewUtils plugin;

    public FurnitureModule(final MewUtils plugin) {
        this.plugin = plugin;
        this.plugin.bindModule(new FurnitureListener(plugin));
    }

}
