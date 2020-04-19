package co.mcsky;

import co.mcsky.MoeUtils;
import org.bukkit.ChatColor;

public class ConfigPrinter {

    private MoeUtils moe;

    public ConfigPrinter(MoeUtils moe) {
        this.moe = moe;
        print();
    }

    private void print() {
        String bullet = " - ";

        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds - Enabled BLocks:");
        moe.foundDiamondsConfig.blocks.forEach(
                (e) -> moe.getLogger().info(bullet + e.toString())
                                                   );

        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds - Enabled Worlds:");
        moe.foundDiamondsConfig.blocks.forEach(
                (e) -> moe.getLogger().info(bullet + e)
                                                   );

        moe.getLogger().info(ChatColor.YELLOW + "MobArena-Addon - Whitelisted Entities:");
        moe.mobArenaProConfig.whitelist.forEach(
                e -> moe.getLogger().info(bullet + e.toString())
                                                    );
    }

}
