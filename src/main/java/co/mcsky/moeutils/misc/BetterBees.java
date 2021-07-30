package co.mcsky.moeutils.misc;

import co.mcsky.moeutils.MoeUtils;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static co.mcsky.moeutils.MoeUtils.plugin;
import static org.bukkit.Material.BEEHIVE;
import static org.bukkit.Material.BEE_NEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class BetterBees implements TerminableModule {

    // cooldown map for message reminder
    private final CooldownMap<Player> messageReminderCooldown;

    public BetterBees() {
        messageReminderCooldown = CooldownMap.create(Cooldown.of(5, TimeUnit.MINUTES));
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (MoeUtils.logActiveStatus("BetterBees", plugin.config.better_bees_enabled)) return;

        /*
          When a player right clicks any beehive or bee nest, we send messages
          about the number of bees inside the block to the player.
         */
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> !e.isBlockInHand())
                .filter(e -> e.getHand() == HAND)
                .handler(e -> {
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
                }).bindWith(consumer);


        /*
          When a player places a beehive or bee nest, we send messages to the
          player to remind them of how to view the number of bees inside the
          block.
         */
        Events.subscribe(BlockPlaceEvent.class)
                .filter(e -> isBeehive(e.getBlockPlaced().getType()))
                .handler(e -> {
                    final Player player = e.getPlayer();
                    if (messageReminderCooldown.test(player)) {
                        player.sendMessage(plugin.getMessage(player, "better-bees.reminder-on-place"));
                    }
                }).bindWith(consumer);
    }

    private void sendMessage(Beehive beehive, Player player) {
        // Depending on whether the player is interacting with bee nest or beehive
        int beeCount = beehive.getEntityCount();
        if (beehive.getType() == BEE_NEST) {
            player.sendMessage(plugin.getMessage(player, "better-bees.count-bee-nest", "bee_count", beeCount));
        } else {
            player.sendMessage(plugin.getMessage(player, "better-bees.count0beehive", "bee_count", beeCount));
        }
    }

    private boolean isSneaking(Player player) {
        return player.isSneaking() || !plugin.config.require_sneak;
    }

    private boolean isBeehive(Material type) {
        return type == BEE_NEST || type == BEEHIVE;
    }
}