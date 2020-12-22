package co.mcsky.moeutils.bees;

import co.mcsky.moeutils.Configuration;
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

import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class BetterBees implements Listener {

    public static final String header = "betterbees";

    public static boolean enable;
    public static boolean requireSneak;

    public final MoeUtils plugin;
    public final Configuration config;

    public BetterBees(MoeUtils plugin) {
        this.plugin = plugin;
        config = plugin.config;

        // Configuration values
        enable = config.node(header, "enable").getBoolean();
        requireSneak = config.node("betterbees", "requireSneak").getBoolean();

        if (enable) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("BetterBees is enabled.");
        }
    }

    /**
     * When a player right clicks any beehive or bee nest, we send messages
     * about the number of bees inside the block to the player.
     */
    @EventHandler
    public void viewBeeCount(PlayerInteractEvent e) {
        if (!e.isBlockInHand()) {
            if (e.getHand() == HAND) {
                if (e.getAction() == RIGHT_CLICK_BLOCK) {
                    // Check block clicked

                    Player player = e.getPlayer();
                    if (isBeehive(e.getClickedBlock().getType()) && isSneaking(player)) {
                        sendMessage((Beehive) e.getClickedBlock().getState(), player);
                    }
                } else if (e.getAction() == RIGHT_CLICK_AIR) {
                    // Check item in hand

                    ItemStack item = e.getItem();
                    Player player = e.getPlayer();
                    if (isBeehive(item.getType()) && isSneaking(player)) {
                        sendMessage((Beehive) item.getItemMeta(), player);
                    }
                }
            }
        }
    }

    private void sendMessage(Beehive beehive, Player player) {
        // Depending on whether the player is interacting with bee nest or beehive
        String raw = beehive.getType() == BEE_NEST
                     ? plugin.getMessage(player, "betterbees.count_bee_nest")
                     : plugin.getMessage(player, "betterbees.count_beehive");
        int beeCount = beehive.getEntityCount();
        player.sendMessage(plugin.getMessage(player, raw, "{bee_count}", String.valueOf(beeCount)));
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
        return player.isSneaking() || !requireSneak;
    }

    private boolean isBeehive(Material type) {
        return type == BEE_NEST || type == BEEHIVE;
    }

}
