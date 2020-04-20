package co.mcsky.bees;

import co.mcsky.MoeUtils;
import co.mcsky.config.BeesConfig;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class BeeCounter implements Listener {

    private final BeesConfig cfg;

    public BeeCounter(MoeUtils moe) {
        cfg = moe.beesCfg;
    }

    @EventHandler
    public void clickBlock(PlayerInteractEvent e) {
        if (!e.isBlockInHand()) {
            // Check block clicked
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

    @EventHandler
    public void useItem(PlayerInteractEvent e) {
        if (e.isBlockInHand()) {
            // Check item in hand
            ItemStack item = e.getItem();
            Material type = item.getType();
            if (e.getHand() == HAND && e.getAction() == RIGHT_CLICK_AIR) {
                if (type == BEEHIVE || type == BEE_NEST) {
                    if (e.getPlayer().isSneaking() || !cfg.requireSneak) {
                        Beehive beehive = (Beehive) ((BlockStateMeta) item.getItemMeta()).getBlockState();
                        e.getPlayer().sendMessage(String.format(type == BEE_NEST
                                                                ? cfg.count_bee_nest
                                                                : cfg.count_beehive, beehive.getEntityCount()));
                    }
                }
            }
        }
    }

}
