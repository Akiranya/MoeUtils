package co.mcsky.bees;

import co.mcsky.MoeUtils;
import co.mcsky.LanguageManager;
import co.mcsky.utilities.CooldownUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BeeReminder implements Listener {

    private final LanguageManager lm;

    public BeeReminder(MoeUtils moe) {
        this.lm = moe.languageManager;
    }

    @EventHandler
    public void placeBeehiveOrBeeNest(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getType() == Material.BEE_NEST || blockPlaced.getType() == Material.BEEHIVE) {
            Player player = event.getPlayer();
            if (CooldownUtil.check(player.getUniqueId(), 15 * 60)) {
                CooldownUtil.use(player.getUniqueId());
                player.sendMessage(lm.betterbees_reminder_on_place);
            }
        }
    }

}
