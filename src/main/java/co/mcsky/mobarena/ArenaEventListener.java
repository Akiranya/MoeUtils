package co.mcsky.mobarena;

import co.mcsky.CustomPlayerName;
import co.mcsky.MoeUtils;
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

import static co.mcsky.CustomPlayerName.ACTION.REMOVE;
import static co.mcsky.CustomPlayerName.ACTION.UPDATE;

public class ArenaEventListener implements Listener {

    private final MoeUtils plugin;
    private MobArena mobArena;
    private Listener PlayerHealthListener;
    private Listener ProjectileCollideListener;
    private HealthBar healthBar;
    private CustomPlayerName custom;

    public ArenaEventListener(MoeUtils plugin, MobArena mobArena, CustomPlayerName custom) {
        this.plugin = plugin;
        this.mobArena = mobArena;
        this.custom = custom;
        this.healthBar = new HealthBar(this.custom, plugin);
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArenaStart(final ArenaStartEvent event) {
        // 游戏开始后，先设置好他们的血条
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                custom.change(p, custom.color("&a&l[&r"), custom.color("&a&l]"), UPDATE);
                healthBar.showHealth(p);
            }
            // 当竞技场开始后，开始监听玩家的血量变化
            PlayerHealthListener = new PlayerHealthListener(plugin, custom);
            ProjectileCollideListener = new ProjectileCollideListener(plugin, mobArena);
        }, 20);
    }

    @EventHandler
    public void onArenaEnd(final ArenaEndEvent event) {
        // 游戏结束，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                custom.change(p, "", "", REMOVE);
                healthBar.removeHealth(p);
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
            custom.change(p, "", "", REMOVE);
            healthBar.removeHealth(p);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerLeave(final ArenaPlayerLeaveEvent event) {
        // 玩家离开，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = event.getPlayer();
            custom.change(event.getPlayer(), "", "", REMOVE);
            healthBar.removeHealth(p);
        }, 20);
    }

}
