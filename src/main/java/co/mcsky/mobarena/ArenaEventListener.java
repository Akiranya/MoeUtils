package co.mcsky.mobarena;

import co.mcsky.MoeUtils;
import co.mcsky.config.MobArenaProConfig;
import co.mcsky.utilities.NametagUtil;
import co.mcsky.utilities.ScoreboardUtil;
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
import org.bukkit.plugin.Plugin;

import static co.mcsky.utilities.NametagUtil.ACTION.REMOVE;
import static co.mcsky.utilities.NametagUtil.ACTION.UPDATE;
import static org.bukkit.Bukkit.getServer;

public class ArenaEventListener implements Listener {

    private final MoeUtils moe;
    private final MobArenaProConfig cfg;
    private final ScoreboardUtil scoreboardUtil;
    private final NametagUtil nametagUtil;
    private MobArena mobArena;
    private Listener PlayerHealthListener;
    private Listener ProjectileCollideListener;

    public ArenaEventListener(MoeUtils moe) {
        this.moe = moe;
        this.cfg = moe.mobArenaProConfig;
        this.nametagUtil = new NametagUtil();
        this.scoreboardUtil = new ScoreboardUtil(this.nametagUtil, moe);

        // Check if MobArena is loaded
        Plugin pluginMobArena = getServer().getPluginManager().getPlugin("MobArena");
        if (pluginMobArena != null) {
            this.mobArena = (MobArena) pluginMobArena;
        }
        // Check ends

        if (cfg.enable && mobArena != null) {
            this.moe.getServer().getPluginManager().registerEvents(this, moe);
            moe.getLogger().info("MobArena-Addon is enabled");
        } else {
            moe.getLogger().warning("MobArena-Addon is disabled as MobArena is not loaded");
        }
    }

    @EventHandler
    public void onArenaStart(final ArenaStartEvent event) {
        // 游戏开始后，先设置好他们的血条
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagUtil.change(p, nametagUtil.color("&a&l[&r"), nametagUtil.color("&a&l]"), UPDATE);
                scoreboardUtil.showHealth(p);
            }
            // 当竞技场开始后，开始监听玩家的血量变化
            PlayerHealthListener = new PlayerHealthListener(moe, nametagUtil);
            ProjectileCollideListener = new ProjectileCollideListener(moe, mobArena);
        }, 20);
    }

    @EventHandler
    public void onArenaEnd(final ArenaEndEvent event) {
        // 游戏结束，清空计分板
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagUtil.change(p, "", "", REMOVE);
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
            nametagUtil.change(p, "", "", REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerLeave(final ArenaPlayerLeaveEvent event) {
        // 玩家离开，清空计分板
        Bukkit.getScheduler().runTaskLater(moe, () -> {
            Player p = event.getPlayer();
            nametagUtil.change(event.getPlayer(), "", "", REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

}
