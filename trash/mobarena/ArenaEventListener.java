package co.mcsky.moeutils.mobarena;

import co.mcsky.moeutils.utilities.NametagUtil;
import co.mcsky.moeutils.utilities.ScoreboardUtil;
import co.mcsky.moeutils.config.Configuration;
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

import static co.mcsky.moeutils.MoeUtils.plugin;

public class ArenaEventListener implements Listener {

    private final ScoreboardUtil scoreboardUtil;
    private final NametagUtil nametagUtil;
    private MobArena mobArena;
    private Listener PlayerHealthListener;
    private Listener ProjectileCollideListener;

    public ArenaEventListener() {
        Configuration config = plugin.config;
        this.nametagUtil = new NametagUtil();
        this.scoreboardUtil = new ScoreboardUtil(this.nametagUtil, plugin);

        // Check if MobArena is loaded
        Plugin pluginMobArena = plugin.getServer().getPluginManager().getPlugin("MobArena");
        if (pluginMobArena != null) {
            this.mobArena = (MobArena) pluginMobArena;
        }
        // Check ends

        if (config.mobarena_enable && mobArena != null) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("MobArena-Addon is enabled.");
        }
    }

    @EventHandler
    public void onArenaStart(final ArenaStartEvent event) {
        // 游戏开始后，先设置好他们的血条
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagUtil.change(p, nametagUtil.color("&a&l[&r"), nametagUtil.color("&a&l]"), NametagUtil.ACTION.UPDATE);
                scoreboardUtil.showHealth(p);
            }
            // 当竞技场开始后，开始监听玩家的血量变化
            PlayerHealthListener = new PlayerHealthListener(plugin, nametagUtil);
            ProjectileCollideListener = new ProjectileCollideListener(mobArena);
        }, 20);
    }

    @EventHandler
    public void onArenaEnd(final ArenaEndEvent event) {
        // 游戏结束，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagUtil.change(p, "", "", NametagUtil.ACTION.REMOVE);
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = event.getPlayer();
            nametagUtil.change(p, "", "", NametagUtil.ACTION.REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerLeave(final ArenaPlayerLeaveEvent event) {
        // 玩家离开，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = event.getPlayer();
            nametagUtil.change(event.getPlayer(), "", "", NametagUtil.ACTION.REMOVE);
            scoreboardUtil.removeHealth(p);
        }, 20);
    }

}
