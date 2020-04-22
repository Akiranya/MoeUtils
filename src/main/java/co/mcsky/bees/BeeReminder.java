package co.mcsky.bees;

import co.mcsky.LanguageRepository;
import co.mcsky.MoeUtils;
import co.mcsky.utilities.CooldownUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BeeReminder implements Listener {

    private final LanguageRepository lang;

    public BeeReminder(MoeUtils moe) {
        this.lang = moe.lang;
    }

    /**
     * When a player places a beehive or bee nest, we send messages to the
     * player to remind them of how to view the number of bees inside the
     * block.
     */
    @EventHandler
    public void placeBeehiveOrBeeNest(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getType() == Material.BEE_NEST || blockPlaced.getType() == Material.BEEHIVE) {
            Player player = event.getPlayer();
            if (CooldownUtil.check(player.getUniqueId(), 15 * 60)) {
                CooldownUtil.use(player.getUniqueId());
                player.sendMessage(lang.betterbees_reminder_on_place);
            }
        }
    }

}
