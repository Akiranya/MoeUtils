package cc.mewcraft.mewutils.module.slimeutility;

import cc.mewcraft.lib.commandframework.bukkit.parsers.PlayerArgument;
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
                    getLang().of("found").send(player);
                } else {
                    getLang().of("not_found").send(player);
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
