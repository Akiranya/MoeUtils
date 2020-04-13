package co.mcsky.config;

import co.mcsky.MoeUtils;
import org.bukkit.ChatColor;

public class ConfigPrinter {

    private MoeUtils moe;

    public ConfigPrinter(MoeUtils moe) {
        this.moe = moe;
        settingOutput();
    }

    private void settingOutput() {
        String bullet = " - ";

        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds - Enabled BLocks:");
        moe.foundDiamondsConfig.getBlocks().forEach(
                (e) -> moe.getLogger().info(bullet + e.toString())
        );

        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds - Enabled Worlds:");
        moe.foundDiamondsConfig.getWorlds().forEach(
                (e) -> moe.getLogger().info(bullet + e)
        );

        moe.getLogger().info(ChatColor.YELLOW + "MobArena-Addon - Whitelisted Entities:");
        moe.mobArenaProConfig.getWhitelist().forEach(
                e -> moe.getLogger().info(bullet + e.toString())
        );
    }

}
