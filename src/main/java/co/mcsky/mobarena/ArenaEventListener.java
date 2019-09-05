package co.mcsky.mobarena;

import co.mcsky.MoeSetting;
import co.mcsky.MoeUtils;
import co.mcsky.util.ScoreboardUtil;
import co.mcsky.util.TagUtil;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static co.mcsky.util.TagUtil.ACTION.REMOVE;
import static co.mcsky.util.TagUtil.ACTION.UPDATE;

public class ArenaEventListener implements Listener {

    private final MoeUtils moe;
    private final MoeSetting setting;
    private final MobArena ma;
    private final ScoreboardUtil scoreboardUtil;
    private final TagUtil th;
    private Listener PlayerHealthListener;
    private Listener ProjectileCollideListener;

    public ArenaEventListener(MoeUtils moe, MobArena ma, TagUtil th) {
        this.moe = moe;
        this.setting = MoeSetting.getInstance(moe);
        this.ma = ma;
        this.th = th;
        this.scoreboardUtil = new ScoreboardUtil(this.th, moe);
        // Only if this feature is enabled do we register this listener
        if (setting.mobarena.enable) {
            this.moe.getServer().getPluginManager().registerEvents(this, moe);
        }
    }

    @EventHandler
    public void onArenaStart(final ArenaStartEvent event) {
        // 游戏开始后，先设置好他们的血条
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                th.change(p, th.color("&a&l[&r"), th.color("&a&l]"), UPDATE);
                scoreboardUtil.showHealth(p);
            }
            // 当竞技场开始后，开始监听玩家的血量变化
            PlayerHealthListener = new PlayerHealthListener(moe, th);
            ProjectileCollideListener = new ProjectileCollideListener(moe, ma);
        }, 20);
    }

    @EventHandler
    public void onArenaEnd(final ArenaEndEvent event) {
        // 游戏结束，清空计分板
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                th.change(p, "", "", REMOVE);
                scoreboardUtil.removeHealth(p);
            }
            // Unregister listener when arena ends
            HandlerList.unregisterAll(PlayerHealthListener);
            HandlerList.unregisterAll(ProjectileCollideListener);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerDeath(final ArenaPlayerDeathEvent event) {
        // 玩家死亡，清空计分板
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            Player p = event.getPlayer();
            th.change(p, "", "", REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerLeave(final ArenaPlayerLeaveEvent event) {
        // 玩家离开，清空计分板
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            Player p = event.getPlayer();
            th.change(event.getPlayer(), "", "", REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

}
