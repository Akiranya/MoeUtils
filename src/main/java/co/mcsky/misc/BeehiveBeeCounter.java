package co.mcsky.misc;

import co.mcsky.MoeUtils;
import co.mcsky.config.BetterBeesConfig;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BeehiveBeeCounter implements Listener {

    private final BetterBeesConfig cfg;

    public BeehiveBeeCounter(MoeUtils moe) {
        cfg = moe.betterBeesConfig;
        if (cfg.isEnable()) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("BeehiveBeeCounter is enabled");
        }
    }

    // TODO 当玩家第一次放下蜂箱或敲掉蜂巢时，提示插件帮助信息

    @EventHandler
    public void onPlayerRightClickBlock(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!e.isBlockInHand()) {
            if (e.getHand() == EquipmentSlot.HAND && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.BEE_NEST || e.getClickedBlock().getType() == Material.BEEHIVE) {
                    String msg = String.format(
                            e.getClickedBlock().getType() == Material.BEE_NEST ? cfg.getCount_bee_nest() : cfg.getCount_beehive(),
                            ((Beehive) e.getClickedBlock().getState()).getEntityCount()
                    );
                    e.getPlayer().sendMessage(msg);
                }
            }
        }
    }

}
