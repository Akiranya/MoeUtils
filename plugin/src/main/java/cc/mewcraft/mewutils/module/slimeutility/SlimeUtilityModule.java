package cc.mewcraft.mewutils.module.slimeutility;

import cc.mewcraft.lib.commandframework.bukkit.parsers.PlayerArgument;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.entity.Player;

public class SlimeUtilityModule extends ModuleBase {

    @Inject
    public SlimeUtilityModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() {
        registerCommand(commandRegistry -> commandRegistry
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("slimechunk")
            .argument(PlayerArgument.of("player"))
            .handler(context -> {
                Player player = context.get("player");
                if (player.getChunk().isSlimeChunk()) {
                    MewUtils.translations().of("slime_chunk.found").send(player);
                } else {
                    MewUtils.translations().of("slime_chunk.not-found").send(player);
                }
            })
        );
    }

    @Override public String getId() {
        return "slimeutil";
    }

    @Override public boolean canEnable() {
        return true;
    }

}
