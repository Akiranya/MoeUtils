package co.mcsky.moeutils.bees;

import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.utilities.CooldownManager;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class BetterBees implements Listener {

    public final MoeUtils plugin;

    public BetterBees(MoeUtils plugin) {
        this.plugin = plugin;
        if (plugin.config.BETTERBEES_ENABLE) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("BetterBees is enabled.");
        }
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
                Material type = e.getClickedBlock().getType();
                Player player = e.getPlayer();
                if (isBeehive(type) && isSneaking(player)) {
                    // Depending on whether the player is interacting with bee nest or beehive
                    String raw = type == BEE_NEST
                                 ? plugin.getMessage(player, "betterbees.count_bee_nest")
                                 : plugin.getMessage(player, "betterbees.count_beehive");
                    int beeCount = ((Beehive) e.getClickedBlock().getState()).getEntityCount();
                    player.sendMessage(plugin.getMessage(player, raw, "{bee_count}", String.valueOf(beeCount)));
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
                    // Depending on whether the player is interacting with bee nest or beehive
                    String raw = type == BEE_NEST
                                 ? plugin.getMessage(player, "betterbees.count_bee_nest")
                                 : plugin.getMessage(player, "betterbees.count_beehive");
                    int beeCount = ((Beehive) ((BlockStateMeta) item.getItemMeta()).getBlockState()).getEntityCount();
                    player.sendMessage(plugin.getMessage(player, raw, "{bee_count}", String.valueOf(beeCount)));
                }
            }
        }
    }

    /**
     * When a player places a beehive or bee nest, we send messages to the
     * player to remind them of how to view the number of bees inside the
     * block.
     */
    @EventHandler
    public void placeBeehiveOrBeeNest(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (isBeehive(blockPlaced.getType())) {
            Player player = event.getPlayer();
            if (CooldownManager.check(player.getUniqueId(), 15 * 60)) {
                CooldownManager.use(player.getUniqueId());
                player.sendMessage(plugin.getMessage(player, "betterbees.reminder_on_place"));
            }
        }
    }

    private boolean isSneaking(Player player) {
        return player.isSneaking() || !plugin.config.BETTERBEES_REQUIRE_SNEAK;
    }

    private boolean isBeehive(Material type) {
        return type == BEE_NEST || type == BEEHIVE;
    }

}
