package cc.mewcraft.mewutils.module.betterbeehive;

import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.listener.AutoCloseableListener;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class BeehiveListener implements AutoCloseableListener {

    private final CooldownMap<Player> messageReminderCooldown;

    public BeehiveListener() {
        this.messageReminderCooldown = CooldownMap.create(Cooldown.of(5, TimeUnit.MINUTES));
    }

    @EventHandler
    public void onClickBeeHive(PlayerInteractEvent event) {
        // When a player right clicks any beehive or bee nest, tell the number of bees inside the block

        if (event.isBlockInHand())
            return;
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Check block clicked

            Player player = event.getPlayer();
            if (event.getClickedBlock() != null && isBeehive(event.getClickedBlock().getType()) && isSneaking(player)) {
                tellCount((Beehive) event.getClickedBlock().getState(), player);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            // Check item in hand

            ItemStack item = event.getItem();
            Player player = event.getPlayer();
            if (item != null && isBeehive(item.getType()) && isSneaking(player)) {
                tellCount((Beehive) item.getItemMeta(), player);
            }
        }
    }

    @EventHandler
    public void onPlaceBeeHive(BlockPlaceEvent event) {
        // When a player places a beehive or bee nest, tell how to view the number of bees inside the block

        if (!isBeehive(event.getBlockPlaced().getType()))
            return;

        Player player = event.getPlayer();
        if (this.messageReminderCooldown.test(player)) {
            MewUtils.translations().of("better_bees.reminder-on-place").send(player);
        }
    }

    private boolean isSneaking(Player player) {
        return player.isSneaking() || !MewUtils.config().require_sneak;
    }

    private boolean isBeehive(Material type) {
        return type == Material.BEE_NEST || type == Material.BEEHIVE;
    }

    private void tellCount(Beehive beehive, Player player) {
        int beeCount = beehive.getEntityCount();
        // Depending on whether the player is interacting with bee nest or bee hive
        if (beehive.getType() == Material.BEE_NEST)
            MewUtils.translations().of("better_bees.count-bee-nest").replace("bee_count", beeCount).send(player);
        else
            MewUtils.translations().of("better_bees.count-beehive").replace("bee_count", beeCount).send(player);
    }

}
