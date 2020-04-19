package co.mcsky.misc;

import co.mcsky.MoeUtils;
import co.mcsky.config.BetterBeesConfig;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class BeehiveBeeCounter implements Listener {

    private final BetterBeesConfig cfg;

    public BeehiveBeeCounter(MoeUtils moe) {
        cfg = moe.betterBeesConfig;
        if (cfg.enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("BeehiveBeeCounter is enabled");
        }
    }

    @EventHandler
    public void onPlayerRightClickBlock(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!e.isBlockInHand()) {
            if (e.getHand() == HAND && e.getAction() == RIGHT_CLICK_BLOCK) {
                Material clickedBlockType = e.getClickedBlock().getType();
                if (clickedBlockType == BEE_NEST || clickedBlockType == BEEHIVE) {
                    e.getPlayer().sendMessage(String.format(
                            clickedBlockType == BEE_NEST
                            ? cfg.count_bee_nest
                            : cfg.count_beehive,
                            ((Beehive) e.getClickedBlock().getState()).getEntityCount()));
                }
            }
        }
    }

}
