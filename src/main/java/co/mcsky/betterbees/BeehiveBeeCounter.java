package co.mcsky.betterbees;

import co.mcsky.MoeSetting;
import co.mcsky.MoeUtils;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BeehiveBeeCounter implements Listener {

    private final MoeUtils moe;
    private final MoeSetting setting;


    public BeehiveBeeCounter(MoeUtils moe) {
        this.moe = moe;
        this.setting = MoeSetting.getInstance(moe);
        if (setting.betterbees.enable) {
            moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    // TODO 当玩家第一次放下蜂箱或敲掉蜂巢时，提示插件帮助信息

    @EventHandler
    public void onPlayerRightClickBlock(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!e.isBlockInHand() &&
                e.getHand() == EquipmentSlot.HAND &&
                (e.getClickedBlock().getType() == Material.BEE_NEST ||
                        e.getClickedBlock().getType() == Material.BEEHIVE)) {
            Beehive beehive = (Beehive) e.getClickedBlock().getState();
            String msg = String.format(setting.betterbees.msg_count, beehive.getEntityCount());
            e.getPlayer().sendMessage(msg);
        }
    }

}
