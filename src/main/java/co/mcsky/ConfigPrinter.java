package co.mcsky;

import org.bukkit.ChatColor;

public class ConfigPrinter {

    private final MoeUtils moe;

    public ConfigPrinter(MoeUtils moe) {
        this.moe = moe;
        print();
    }

    private void print() {
        final String bullet = " - ";
        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds.blocks:");
        moe.foundDiamondsCfg.blocks.forEach(e -> moe.getLogger().info(bullet + e.toString().toLowerCase()));
        moe.getLogger().info(ChatColor.YELLOW + "FoundDiamonds.worlds:");
        moe.foundDiamondsCfg.worlds.forEach(e -> moe.getLogger().info(bullet + e));
        moe.getLogger().info(ChatColor.YELLOW + "MobArena-Addon.whitelist:");
        moe.mobArenaProCfg.whitelist.forEach(e -> moe.getLogger().info(bullet + e.toString().toLowerCase()));
    }

}
