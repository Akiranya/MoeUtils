package co.mcsky.moeutils.mobarena;

import co.mcsky.moeutils.Configuration;
import co.mcsky.moeutils.utilities.NametagManager;
import co.mcsky.moeutils.utilities.ScoreboardManager;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static co.mcsky.moeutils.MoeUtils.plugin;

public class MobArenaAddon implements Listener {

    public static boolean enable;
    public static Set<EntityType> whiteList;

    @Getter private MobArena mobArena;
    private ScoreboardManager scoreboardManager;
    private NametagManager nametagManager;

    private Listener PlayerHealthListener;
    private Listener ProjectileCollideListener;

    @SuppressWarnings("SimplifyStreamApiCallChains")
    public MobArenaAddon() {
        Configuration config = plugin.config;

        // Configuration values
        enable = config.node("mobarena", "enable").getBoolean();
        try {
            whiteList = config.node("mobarena", "whitelist").getList(EntityType.class, () ->
                    List.of(EntityType.PLAYER,
                            EntityType.WOLF,
                            EntityType.OCELOT,
                            EntityType.IRON_GOLEM,
                            EntityType.HORSE)).stream().collect(Collectors.toSet());
        } catch (SerializationException e) {
            plugin.getLogger().severe("MobArenaAddon initialization failed! Please validate the configuration.");
            return;
        }

        nametagManager = new NametagManager();
        scoreboardManager = new ScoreboardManager(nametagManager);

        // Check if MobArena is loaded
        Plugin mobArena = plugin.getServer().getPluginManager().getPlugin("MobArena");
        if (mobArena != null) {
            this.mobArena = (MobArena) mobArena;
        }

        if (enable && this.mobArena != null) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("MobArena-Addon is enabled.");
        }
    }

    @EventHandler
    public void onArenaStart(final ArenaStartEvent event) {
        // 游戏开始后，先设置好他们的血条
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagManager.change(p, nametagManager.color("&a&l[&r"), nametagManager.color("&a&l]"), NametagManager.ACTION.UPDATE);
                scoreboardManager.showHealth(p);
            }
            // 当竞技场开始后，开始监听玩家的血量变化
            PlayerHealthListener = new PlayerHealthListener(nametagManager);
            ProjectileCollideListener = new ProjectileCollideListener(this);
        }, 20);
    }

    @EventHandler
    public void onArenaEnd(final ArenaEndEvent event) {
        // 游戏结束，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : event.getArena().getAllPlayers()) {
                nametagManager.change(p, "", "", NametagManager.ACTION.REMOVE);
                scoreboardManager.removeHealth(p);
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
            nametagManager.change(p, "", "", NametagManager.ACTION.REMOVE);
            scoreboardManager.removeHealth(p);
        }, 20);
    }

    @EventHandler
    public void onArenaPlayerLeave(final ArenaPlayerLeaveEvent event) {
        // 玩家离开，清空计分板
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = event.getPlayer();
            nametagManager.change(event.getPlayer(), "", "", NametagManager.ACTION.REMOVE);
            scoreboardManager.removeHealth(p);
        }, 20);
    }

}
