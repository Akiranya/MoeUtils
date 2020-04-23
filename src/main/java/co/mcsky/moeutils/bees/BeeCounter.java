package co.mcsky.moeutils.bees;

import co.mcsky.moeutils.LanguageRepository;
import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.config.Configuration;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
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

    private final Configuration config;
    private final LanguageRepository lang;

    public BeeCounter() {
        this.config = MoeUtils.plugin.config;
        this.lang = MoeUtils.plugin.lang;
    }

    /**
     * When a player right clicks any beehive or bee nest, we send messages
     * about the number of bees inside the block to the player.
     */
    @EventHandler
    public void clickBlock(PlayerInteractEvent e) {
        if (!e.isBlockInHand()) {
            // Check block clicked
            if (e.getHand() == HAND && e.getAction() == RIGHT_CLICK_BLOCK) {
                Material clickedBlockType = e.getClickedBlock().getType();
                Player player = e.getPlayer();
                if (isBeehive(clickedBlockType) && isSneaking(player)) {
                    player.sendMessage(String.format(
                            clickedBlockType == BEE_NEST
                            ? lang.betterbees_count_bee_nest
                            : lang.betterbees_count_beehive,
                            ((Beehive) e.getClickedBlock().getState()).getEntityCount()));
                }
            }
        }
    }

    /**
     * When a player uses the beehives or bee nests in their hands, we send
     * messages about the number of bees inside the blocks to the player.
     */
    @EventHandler
    public void useItem(PlayerInteractEvent e) {
        if (e.isBlockInHand()) {
            // Check item in hand
            if (e.getHand() == HAND && e.getAction() == RIGHT_CLICK_AIR) {
                ItemStack item = e.getItem();
                Material type = item.getType();
                Player player = e.getPlayer();
                if (isBeehive(type) && isSneaking(player)) {
                    player.sendMessage(String.format(
                            type == BEE_NEST
                            ? lang.betterbees_count_bee_nest
                            : lang.betterbees_count_beehive,
                            ((Beehive) ((BlockStateMeta) item.getItemMeta()).getBlockState()).getEntityCount()));
                }
            }
        }
    }

    private boolean isSneaking(Player player) {
        return player.isSneaking() || !config.betterbees_requireSneak;
    }

    private boolean isBeehive(Material type) {
        return type == BEE_NEST || type == BEEHIVE;
    }

}